package ssca.validator

import java.io.File

import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import gitCrawler.{Commit, Fault, Repo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.{FunctionMetric, Metric, ObjectMetric}

import scala.concurrent.Lock
import scala.io.Source

/**
  * Created by erikl on 4/25/2017.
  */
class Validator(repoUser: String, repoName: String, repoPath: String, instances: Int,
                instanceThreads: Int, metrics: List[Metric], labels: List[String]) {

  private val token = loadToken()

  private val OutputDir = repoPath + "Output"
  private val fullOutput = OutputDir + "\\fullOutput.csv"
  private val faultOutput = OutputDir + "\\faultOutput.csv"
  private val outputLock = new Lock

  private val instanceIds: List[Int] = List.range(0, instances)

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
    Output.writeOutput(List("commit, faults, path, " + objHeader.mkString(", ")), fullOutput)
    Output.writeOutput(List("commit, faults, path, " + objHeader.mkString(", ")), faultOutput)
  }

  def writeFunctionHeader(): Unit = {
    val (_, funHeader) = metricsHeader()
    Output.writeOutput(List("commit, faults, path, " + funHeader.mkString(", ")), fullOutput)
    Output.writeOutput(List("commit, faults, path, " + funHeader.mkString(", ")), faultOutput)
  }

  def writeHeaders(): Unit = {
    val (objHeader, funHeader) = metricsHeader()
    val header = objHeader:::funHeader
    Output.writeOutput(List("commit, faults, path, " + header.mkString(", ")), fullOutput)
    Output.writeOutput(List("commit, faults, path, " + header.mkString(", ")), faultOutput)
  }


  private def createMetrics(): List[Metric] = {
    metrics.foldLeft(List[Metric]())((a, b) => a ::: List(b.newInstance()))
  }


  def run(wh: () => Unit, op: (String, Fault, List[ResultUnit]) => Unit): Unit = {
    createOutputFile()
    wh()
    instanceIds.par.foreach(runInstance(_, op))
  }

  private def runInstance(id: Int, op: (String, Fault, List[ResultUnit]) => Unit): Unit = {
    val instancePath = repoPath + id

    /* Init the repo for the instance */
    val repo = new Repo(repoUser, repoName, token, List("bug", "failed", "needs-attention "), instancePath)
    println("Done loading repo: " + id)

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), instancePath, instanceThreads)
    println("Done init codeAnalysis.analyser: " + id)

    /* Get the faults and select the correct chunk. */
    val faults = repo.faults
    val chunk = faults.grouped(math.ceil(faults.length.toDouble / instances).toInt).toList(id)

    var count = 0
    var prevCommit: Commit = null

    /* Analyse each fault. */
    chunk.foreach {
      x =>
        /* Commit to previous commit. */
        repo.checkoutPreviousCommit(x.commit)
        an.refresh()

        /* Get the result. */
        val results = if (prevCommit != null) {
          val files = repo.changedFiles(prevCommit, x.commit).map(x => instancePath + "\\" + x)
          an.analyse(files)
        } else {
          an.analyse()
        }

        /* Run output function. */
        op(instancePath, x, results)

        prevCommit = x.commit
        count += 1
        println(count + "/" + chunk.length + ":  " + results.length + " => " + id)
    }
  }


  def objectOutput(instancePath: String, fault: Fault, results: List[ResultUnit]): Unit = {
    val header: List[String] = getObjectHeaders ::: getFunctionHeaders
    results.foreach {
      y =>
        val lines = fault.commit.getPatchData(y.position.source.path.substring(instancePath.length + 1).replace("\\", "/"))
        y.results.foreach {
          case obj: ObjectResult =>
            lines match {
              case Some(patch) =>
                outputLock.acquire()
                if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                  Output.writeOutput(obj.toCsvObjectAvr(header.length).map(fault.commit.sha + ", " + fault.issues.length + ", " + _),  faultOutput)
                  Output.writeOutput(obj.toCsvObjectAvr(header.length).map(fault.commit.sha + ", " + fault.issues.length + ", " + _), fullOutput)
                }else{
                  Output.writeOutput(obj.toCsvObjectAvr(header.length).map(fault.commit.sha + ", " + 0 + ", " + _), fullOutput)
                }
                outputLock.release()
              case _ =>
                outputLock.acquire()
                Output.writeOutput(obj.toCsvObjectAvr(header.length).map(fault.commit.sha + ", " + 0 + ", " + _), fullOutput)
                outputLock.release()
            }
        }
    }
  }

}
