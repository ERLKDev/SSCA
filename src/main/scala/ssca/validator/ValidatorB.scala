package ssca.validator

import java.io.File
import java.util.Date

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
class ValidatorB(repoUser: String, repoName: String, repoPath: String, instances: Int,
                 instanceThreads: Int, metrics: List[Metric], labels: List[String]) {

  private val token = loadToken()

  private val outputLock = new Lock
  private val instanceIds: List[Int] = List.range(0, instances)

  private val OutputDir = repoPath + "Output"

  createOutputDir()

  private val fullOutput = new Output(OutputDir + "\\fullOutput.csv", true)
  private val faultOutput = new Output(OutputDir + "\\faultOutput.csv", true)

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
    funHeader
  }

  def writeObjectHeaders(): Unit = {
    val (objHeader, _) = metricsHeader()
    fullOutput.writeOutput(List("commit,faults,path," + objHeader.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path," + objHeader.mkString(",")))
  }

  def writeFunctionHeader(): Unit = {
    val (_, funHeader) = metricsHeader()
    fullOutput.writeOutput(List("commit,faults,path," + funHeader.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path," + funHeader.mkString(",")))
  }

  def writeHeaders(): Unit = {
    val (objHeader, funHeader) = metricsHeader()
    val header = objHeader:::funHeader
    fullOutput.writeOutput(List("commit,faults,path," + header.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path," + header.mkString(",")))
  }


  private def createMetrics(): List[Metric] = {
    metrics.foldLeft(List[Metric]())((a, b) => a ::: List(b.newInstance()))
  }


  def run(wh: () => Unit, op: (String, Fault, List[ResultUnit]) => (List[String], List[String])): Unit = {
    wh()
    totalCount = 0

    val repoInfo = new RepoInfo(repoUser, repoName, token, labels, "master", repoPath)
    instanceIds.par.foreach(runInstance(_, repoInfo, op))
    faultOutput.close()
    fullOutput.close()
    Http.shutdown()
  }

  private def runInstance(id: Int, repoInfo: RepoInfo, op: (String, Fault, List[ResultUnit]) => (List[String], List[String])): Unit = {
    val instancePath = repoPath + id

    /* Init the repo for the instance */
    println("Start init repo: " + id)
    val repo = new Repo(repoUser, repoName, instancePath, repoInfo)
    println("Done loading repo: " + id)

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), instancePath, instanceThreads, true)
    println("Done init codeAnalysis.analyser: " + id)

    /* Get the faults and select the correct chunk. */
    val faults = repoInfo.faults.filter(x => x.commit.isBetween(new Date(116, 9, 30), new Date(117, 5, 9)))
    val chunk = faults.grouped(math.ceil(faults.length.toDouble / instances).toInt).toList(id)

    var count = 0
    var prevCommit: Commit = null

    println("Start => " + id)
    /* Analyse each fault. */
    chunk.foreach {
      x =>
        /* Commit to previous commit. */
        repo.checkoutPreviousCommit(x.commit)
        an.refresh()


        /* Get the result. */
        val results = if (prevCommit != null) {
          val files = repo.changedFiles(prevCommit, x.commit).map(x => instancePath + "\\" + x)  ::: x.commit.files.map(instancePath + "\\" + _)
          an.analyse(files)
        } else {
          println("analyse")
          an.analyse()
        }

        /* Run output function. */
        val output = op(instancePath, x, results)

        count += 1

        outputLock.acquire()
        faultOutput.writeOutput(output._1)
        fullOutput.writeOutput(output._2)
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
    }
    println(id + " Done!")
    an.close()
  }


  def objectOutput(instancePath: String, fault: Fault, results: List[ResultUnit]): (List[String], List[String]) = {
    val header: List[String] = getObjectHeaders ::: getFunctionHeaders
    results.foldLeft((List[String](), List[String]())) {
      (r, y) =>
        val lines = fault.commit.getPatchData(y.position.source.path.substring(instancePath.length + 1).replace("\\", "/"))
        val res = y.results.foldLeft((List[String](), List[String]())) {
          (a, b) =>
            b match {
              case obj: ObjectResult =>
                lines match {
                  case Some(patch) =>
                    if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                      (a._1 ::: obj.toCsvObjectAvr(header.length).map(fault.commit.sha + "," + 1 + "," + _),
                        a._2 ::: obj.toCsvObjectAvr(header.length).map(fault.commit.sha + "," + 1 + "," + _))
                    } else {
                      (a._1, a._2 ::: obj.toCsvObjectAvr(header.length).map(fault.commit.sha + "," + 0 + "," + _))
                    }
                  case _ =>
                    (a._1, a._2 ::: obj.toCsvObjectAvr(header.length).map(fault.commit.sha + "," + 0 + "," + _))
                }
            }
        }
        (r._1 ::: res._1, r._2 ::: res._2)
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
