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
  private val fullOutput = OutputDir + "\\fullOutput.csv"
  private val faultOutput = OutputDir + "\\faultOutput.csv"


  private def loadToken(): String = {
    val tokenFile = Source.fromFile("github.token")
    val githubToken = tokenFile.getLines.mkString
    tokenFile.close()
    githubToken
  }

  private def createOutputFile(): Unit = {
    val outputDir = new File(OutputDir)
    outputDir.mkdirs()

    val fullOutputFile = new File(fullOutput)
    fullOutputFile.delete()
    fullOutputFile.createNewFile()

    val faultOutputFile = new File(faultOutput)
    faultOutputFile.delete()
    faultOutputFile.createNewFile()
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
    Output.writeOutput(List("commit,faults,path, " + objHeader.mkString(",")), fullOutput)
    Output.writeOutput(List("commit,faults,path, " + objHeader.mkString(",")), faultOutput)
  }

  def writeFunctionHeader(): Unit = {
    val (_, funHeader) = metricsHeader()
    Output.writeOutput(List("commit,faults,path, " + funHeader.mkString(",")), fullOutput)
    Output.writeOutput(List("commit,faults,path, " + funHeader.mkString(",")), faultOutput)
  }

  def writeHeaders(): Unit = {
    val (objHeader, funHeader) = metricsHeader()
    val header = objHeader:::funHeader
    Output.writeOutput(List("commit,faults,path, " + header.mkString(",")), fullOutput)
    Output.writeOutput(List("commit,faults,path, " + header.mkString(",")), faultOutput)
  }


  private def createMetrics(): List[Metric] = {
    metrics.foldLeft(List[Metric]())((a, b) => a ::: List(b.newInstance()))
  }


  def run(wh: () => Unit): Unit = {
    createOutputFile()
    wh()
    val repoInfo = new RepoInfo(repoUser, repoName, token, List("bug", "failed", "needs-attention "), "master", repoPath)
    val instancePath = repoPath + "0"

    /* Init the repo for the instance */
    println("Start init repo")
    val repo = new Repo(repoUser, repoName, instancePath, repoInfo)
    println("Done loading repo")

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), instancePath, instanceThreads)
    println("Done init codeAnalysis.analyser")

    /* Get the faults and select the correct chunk. */
    val faults = repoInfo.faults
    val faultyFiles = faults.foldLeft(List[String]())((a, b) => a ::: b.commit.files)


    val results = an.analyse()

    val header: List[String] = getObjectHeaders ::: getFunctionHeaders

    results.foreach{
      y =>
        y.results.foreach {
          case obj: ObjectResult =>
            val count = faultyFiles.count(x => x.equals(obj.position.source.path.substring(instancePath.length + 1).replace("\\", "/")))
            Output.writeOutput(obj.toCsvObjectAvr(header.length).map("HEAD," + count + "," + _), fullOutput)
        }
    }

  }
}
