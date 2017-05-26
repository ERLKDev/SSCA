package ssca.validator

import java.io.File

import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import dispatch.Http
import gitCrawler.{Commit, Fault, Repo, RepoInfo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.{FunctionMetric, Metric, ObjectMetric}

import scala.concurrent.Lock
import scala.io.Source

/**
  * Created by erikl on 4/25/2017.
  */
class OOValidator(repoUser: String, repoName: String, repoPath: String, instances: Int,
                  instanceThreads: Int, metrics: List[Metric], labels: List[String]) {

  private val token = loadToken()

  private val outputLock = new Lock
  private val instanceIds: List[Int] = List.range(0, instances)

  private val OutputDir = repoPath + "OutputOO"

  createOutputDir()

  private val fullOutput = new Output(OutputDir + "\\fullOutputOO.csv", true)
  private val faultOutput = new Output(OutputDir + "\\faultOutputOO.csv", true)

  private var totalCount = 0

  private def loadToken(): String = {
    val tokenFile = Source.fromFile("github.token")
    val githubToken = tokenFile.getLines.mkString
    tokenFile.close()
    githubToken
  }

  private def createOutputDir(): Unit = {
    val outputDir = new File(OutputDir)
    outputDir.mkdirs()
  }

  private def metricsHeader(): (List[String], List[String]) = {
    val (objHeader, funHeader) = metrics.foldLeft((List[String](), List[String]())){
      (a, b) =>
        a match {
          case (i, k) =>
            if (b.isInstanceOf[ObjectMetric] && b.isInstanceOf[FunctionMetric])
              (i:::b.asInstanceOf[ObjectMetric].objectHeader, k:::b.asInstanceOf[FunctionMetric].functionHeader)
            else
              b match {
                case x: ObjectMetric =>
                  (i:::x.objectHeader, k)
                case x: FunctionMetric =>
                  (i, k:::x.functionHeader)
              }
        }
    }

    (objHeader.sortWith(_ < _), funHeader.sortWith(_ < _))
  }

  private def getObjectHeaders: List[String] = {
    val (objHeader, _) = metricsHeader()
    objHeader
  }

  private def getFunctionHeaders: List[String] = {
    val (_, funHeader) = metricsHeader()
    funHeader.map("functionAvr" + _.capitalize) ::: funHeader.map("functionSum" + _.capitalize)
  }

  def writeHeaders(): Unit = {
    val objHeader = getObjectHeaders
    val funHeader = getFunctionHeaders
    val header = objHeader:::funHeader
    fullOutput.writeOutput(List("commit,faults,path," + header.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path," + header.mkString(",")))
  }


  private def createMetrics(): List[Metric] = {
    metrics.foldLeft(List[Metric]())((a, b) => a ::: List(b.newInstance()))
  }


  def run(wh: () => Unit, op: (String, Fault, List[ResultUnit]) => List[String]): Unit = {
    wh()
    totalCount = 0

    val repoInfo = new RepoInfo(repoUser, repoName, token, labels, "master", repoPath)
    val faultyClasses = instanceIds.par.map(x => runInstance(x, repoInfo, op)).foldLeft(List[String]())((a, b) => a ::: b)


    println("Start init repo")
    val repo = new Repo(repoUser, repoName, repoPath + "0", repoInfo)
    println("Done loading repo")


    val an = new Analyser(createMetrics(), repoPath + "0", instanceThreads)
    println("Done init analyser")

    repo.checkoutHead()
    an.refresh()

    val results = an.analyse()

    val header: List[String] = getObjectHeaders ::: getFunctionHeaders

    val tmpOutput = results.foldLeft(List[String]()){
      (r, y) =>
        r ::: y.results.foldLeft(List[String]()) {
          (a, b) =>
            b match {
              case obj: ObjectResult =>
                val count = faultyClasses.count(x => x == obj.objectPath.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath))
                a ::: List("HEAD," + count + "," + obj.toCSV(header.length))
            }
        }
    }
    val output = tmpOutput.map(x => x.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath))

    fullOutput.writeOutput(output)

    faultOutput.close()
    fullOutput.close()
    Http.shutdown()
  }

  private def runInstance(id: Int, repoInfo: RepoInfo, op: (String, Fault, List[ResultUnit]) => List[String]): List[String] = {
    val instancePath = repoPath + id

    /* Init the repo for the instance */
    println("Start init repo: " + id)
    val repo = new Repo(repoUser, repoName, instancePath, repoInfo)
    println("Done loading repo: " + id)

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), instancePath, instanceThreads)
    println("Done init codeAnalysis.analyser: " + id)

    /* Get the faults and select the correct chunk. */
    val faults = repoInfo.faults
    val chunk = faults.grouped(math.ceil(faults.length.toDouble / instances).toInt).toList(id)

    var count = 0
    var prevCommit: Commit = null

    println("Start => " + id)
    /* Analyse each fault. */
    val res = chunk.foldLeft(List[String]()) {
      (r, x) =>
        /* Commit to previous commit. */
        repo.checkoutPreviousCommit(x.commit)
        an.refresh()


        /* Get the result. */
        val results = an.analyse(x.commit.files.map(instancePath + "\\" + _))

        /* Run output function. */
        val output = op(instancePath, x, results).map(x => x.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath))

        count += 1

        outputLock.acquire()
        totalCount += 1
        outputLock.release()

        val nextSha = {
          val index = chunk.indexOf(x) + 1
          if (index < chunk.length)
            chunk(index).commit.sha
          else
            "Last"
        }

        println(id + ":\t" + count + "/" + chunk.length + "(" + (count * 100) / chunk.length + "%)\t\tTotal: "
          + totalCount + "/" + faults.length + "(" + (totalCount * 100) / faults.length + "%)\t\t"
          + results.length + "\t\t=>\t" + x.commit.sha + "\t->\t" + nextSha)

        prevCommit = x.commit
        x.unload()
        r ::: output
    }
    println(id + " Done!")
    an.close()
    res
  }


  def objectOutput(instancePath: String, fault: Fault, results: List[ResultUnit]): List[String] = {
    results.foldLeft(List[String]()) {
      (r, y) =>
        val lines = fault.commit.getPatchData(y.position.source.path.substring(instancePath.length + 1).replace("\\", "/"))
        val res = y.results.foldLeft(List[String]()) {
          (a, b) =>
            b match {
              case obj: ObjectResult =>
                lines match {
                  case Some(patch) =>
                    if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                      a ::: List(obj.objectPath)
                    } else {
                      a
                    }
                  case _ =>
                    a
                }
            }
        }
        r:::res
    }
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Done in: " + (t1 - t0) + "ns (" + ((t1 - t0).toDouble / 1000000000.0) + "seconds)")
    result
  }
}
