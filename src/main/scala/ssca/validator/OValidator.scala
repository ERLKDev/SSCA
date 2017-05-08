package ssca.validator

import java.io.File

import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import gitCrawler.{Commit, Fault, Repo, RepoInfo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.{FunctionMetric, Metric, ObjectMetric}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Lock
import scala.io.Source

/**
  * Created by erikl on 4/25/2017.
  */
class OValidator(repoUser: String, repoName: String, repoPath: String, instanceThreads: Int, metrics: List[Metric], labels: List[String]) {

  private val token = loadToken()

  private val OutputDir = repoPath + "OutputO"
  createOutputDir()

  private val fullOutput = new Output(OutputDir + "\\fullOutputO.csv", true)
  private val faultOutput = new Output(OutputDir + "\\faultOutputO.csv", true)


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
    fullOutput.writeOutput(List("commit,faults,path, " + objHeader.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path, " + objHeader.mkString(",")))
  }

  def writeFunctionHeader(): Unit = {
    val (_, funHeader) = metricsHeader()
    fullOutput.writeOutput(List("commit,faults,path, " + funHeader.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path, " + funHeader.mkString(",")))
  }

  def writeHeaders(): Unit = {
    val (objHeader, funHeader) = metricsHeader()
    val header = objHeader:::funHeader
    fullOutput.writeOutput(List("commit,faults,path, " + header.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path, " + header.mkString(",")))
  }


  private def createMetrics(): List[Metric] = {
    metrics.foldLeft(List[Metric]())((a, b) => a ::: List(b.newInstance()))
  }


  def run(wh: () => Unit): Unit = {
    wh()
    val repoInfo = new RepoInfo(repoUser, repoName, token, labels, "master", repoPath)
    val instancePath = repoPath + "0"

    /* Init the repo for the instance */
    println("Start init repo")
    val repo = new Repo(repoUser, repoName, instancePath, repoInfo)
    println("Done loading repo")

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), instancePath, instanceThreads)
    println("Done init analyser")

    /* Get the faults and select the correct chunk. */
    val faults = repoInfo.faults
    val faultyFiles = faults.foldLeft(List[String]())((a, b) => a ::: b.commit.files)

    repo.checkoutHead()
    an.refresh()

    val results = an.analyse()

    val header: List[String] = getObjectHeaders ::: getFunctionHeaders

    val output = results.foldLeft(List[String]()){
      (r, y) =>
        r ::: y.results.foldLeft(List[String]()) {
          (a, b) =>
            b match {
              case obj: ObjectResult =>
                val count = faultyFiles.count(x => x.equals(obj.position.source.path.substring(instancePath.length + 1).replace("\\", "/")))
                a ::: obj.toCsvObjectAvr(header.length).map("HEAD," + count + "," + _)
            }
        }
    }
    fullOutput.writeOutput(output)
    fullOutput.close()

  }
}
