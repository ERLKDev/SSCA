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
class ValidatorN(repoUser: String, repoName: String, repoPath: String, instances: Int,
                 instanceThreads: Int, metrics: List[Metric], labels: List[String]) {

  private val token = loadToken()

  private val outputLock = new Lock
  private val instanceIds: List[Int] = List.range(0, instances)

  private val OutputDir = repoPath + "Output"

  createOutputDir()

  private val fullOutput = new Output(OutputDir + "\\fullOutput.csv", true)
  private val faultOutput = new Output(OutputDir + "\\faultOutput.csv", true)

  private var totalCount = 0
  private var outputLines = 0

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


  def run() : Unit = {
    writeHeaders()
    totalCount = 0

    val repoInfo = new RepoInfo(repoUser, repoName, token, labels, "master", repoPath)
    val faultyClassesTmp = instanceIds.par.map(runInstance(_, repoInfo)).foldLeft(List[String]())(_ ::: _)
    val faultyClasses = faultyClassesTmp.map(x => x.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath))

    println("Last phase")
    println("Start init repo last phase")
    val repo = new Repo(repoUser, repoName, repoPath + 0, repoInfo)
    println("Done loading repo last phase")

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), repoPath + 0, instanceThreads, false)
    println("Done init analyser last phase")

    println("analyse")
    val results = an.analyse()

    val tmpOutput = objectOutput2(faultyClasses, results)
    val output = tmpOutput.map(x => x.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath))

    println(results.length + "    " + output.length)
    outputLock.acquire()
    fullOutput.writeOutput(output)
    outputLines += output.length
    outputLock.release()

    faultOutput.close()
    fullOutput.close()
    Http.shutdown()

    println("Total output lines: " + outputLines)
  }

  private def runInstance(id: Int, repoInfo: RepoInfo): List[String] = {
    val instancePath = repoPath + id

    /* Init the repo for the instance */
    println("Start init repo: " + id)
    val repo = new Repo(repoUser, repoName, instancePath, repoInfo)
    println("Done loading repo: " + id)

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), instancePath, instanceThreads, false)
    println("Done init analyser: " + id)

    /* Get the faults and select the correct chunk. */
    val faults = repoInfo.faults
    val chunk = faults.grouped(math.ceil(faults.length.toDouble / instances).toInt).toList(id)

    var count = 0

    println("Start => " + id)
    /* Analyse each fault. */
    val faultyFiles = chunk.foldLeft(List[String]()) {
      (a, x) =>
        /* Commit to previous commit. */
        repo.checkoutPreviousCommit(x.commit)
        an.refresh()


        /* Get the result. */
        val results = an.analyse(x.commit.scalaFiles.map(instancePath + "\\" + _))

        /* Run output function. */
        val tmpOutput = objectOutput(instancePath, x, results)
        val output = (
          tmpOutput._1.map(x => x.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath)),
          tmpOutput._2.map(x => x.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath)),
          tmpOutput._3.map(x => x.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath))
        )

        count += 1

        outputLock.acquire()
        if (output._1.nonEmpty)
          faultOutput.writeOutput(output._1)
        if (output._2.nonEmpty)
          fullOutput.writeOutput(output._2)
        totalCount += 1
        outputLines += output._2.length
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

        x.unload()
        a ::: output._3
    }

    println(id + " Done!")
    an.close()
    faultyFiles
  }


  def objectOutput(instancePath: String, fault: Fault, results: List[ResultUnit]): (List[String], List[String], List[String]) = {
    val header: List[String] = getObjectHeaders ::: getFunctionHeaders


    def recursive(result: ResultUnit) : (List[String], List[String], List[String]) =  {
      val lines = fault.commit.getPatchData(result.position.source.path.substring(instancePath.length + 1).replace("\\", "/"))
      result.results.foldLeft((List[String](), List[String](), List[String]())) {
        (a, b) =>
          b match {
            case obj: ObjectResult =>
              val out = recursive(obj)
              lines match {
                case Some(patch) =>
                  if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                    (a._1 ::: List(fault.commit.sha + "," + 1 + "," + obj.toCSV(header.length)),
                      a._2 ::: List(fault.commit.sha + "," + 1 + "," + obj.toCSV(header.length)), obj.objectPath :: a._3)
                  } else {
                    (a._1 ::: out._1, a._2 ::: out._2, a._3 ::: out._3)
                  }
                case _ =>
                  (a._1 ::: out._1, a._2 ::: out._2, a._3 ::: out._3)
              }
            case unit: ResultUnit =>
              val out = recursive(unit)
              (a._1 ::: out._1, a._2 ::: out._2, a._3 ::: out._3)
            case _ =>
              a
          }
      }
    }

    results.foldLeft((List[String](), List[String](), List[String]())) {
      (r, y) =>
        val res = recursive(y)
        (r._1 ::: res._1, r._2 ::: res._2, r._3 ::: res._3)
    }
  }

  def objectOutput2(faultyClasses: List[String], results: List[ResultUnit]): List[String] = {

    val header: List[String] = getObjectHeaders ::: getFunctionHeaders


    def recursive(result: ResultUnit) : List[String] =  {
      result.results.foldLeft(List[String]()) {
        (a, b) =>
          b match {
            case obj: ObjectResult =>
              if (faultyClasses.contains(obj.objectPath.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath))) {
                a ::: recursive(obj)
              } else {
                a ::: List("HEAD," + 0 + "," + obj.toCSV(header.length))::: recursive(obj)
              }
            case unit: ResultUnit =>
              a ::: recursive(unit)

            case _ =>
              a
          }
      }
    }

    results.foldLeft(List[String]()) {
      (r, y) =>
        r ::: recursive(y)
    }
  }
}
