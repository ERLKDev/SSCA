package ssca.validator

import java.io.File

import codeAnalysis.analyser.result._
import main.scala.analyser.metric.{FunctionMetric, Metric, ObjectMetric}
import ssca.validator.output.Output

import scala.concurrent.Lock
import scala.io.Source

/**
  * Created by erikl on 6/1/2017.
  */
abstract class ValidatorBase(path: String, repoUser: String, repoName: String, metrics: List[Metric], outputName: String) {

  /* Paths */
  val repoPath: String = path +"\\" + repoUser.capitalize + repoName.capitalize
  val dataPath: String = repoPath + "Data"
  val outputPath: String = repoPath + "Output"


  /* First create dirs before the outputs. */
  createDirs()


  /* Creates the outputs*/
  private val fullOutput: Output = new Output(outputPath + "\\" + outputName + ".csv", true)
  private val faultOutput: Output = new Output(outputPath + "\\faultOutput.csv", true)


  /* Github API token. Should be in file "github.token" in the main dir */
  val token: String = loadToken()

  /* Create the output locks. */
  private val faultLock: Lock = new Lock
  private val fullLock: Lock = new Lock
  val consoleLock: Lock = new Lock

  /* Writes the output headers to the files */
  writeHeaders()


  /**
    * Creates the output directory
    */
  private def createDirs(): Unit = {
    val dataDir = new File(dataPath)
    val outputDir = new File(outputPath)

    dataDir.mkdirs()
    outputDir.mkdirs()
  }


  /**
    * Loads the Github token from the token file
    * @return
    */
  private def loadToken(): String = {
    val tokenFile = Source.fromFile("github.token")
    val githubToken = tokenFile.getLines.mkString
    tokenFile.close()
    githubToken
  }

  /**
    * Function to create the metric instances
    *
    * @return
    */
  def createMetrics(): List[Metric] = {
    metrics.foldLeft(List[Metric]())((a, b) => a ::: List(b.newInstance()))
  }

  /**
    * Function to get the metric headers
    * @return
    */
  private def getHeaders: (List[String], List[String]) = {
    val (objHeader, funHeader) = metrics.foldLeft((List[String](), List[String]())){
      (res, metric) =>

        /* If metric is both a function as an object metric */
        if (metric.isInstanceOf[ObjectMetric] && metric.isInstanceOf[FunctionMetric])
          (res._1:::metric.asInstanceOf[ObjectMetric].objectHeader, res._2:::metric.asInstanceOf[FunctionMetric].functionHeader)
        else
          metric match {
            case x: ObjectMetric =>
              (res._1:::x.objectHeader, res._2)
            case x: FunctionMetric =>
              (res._1, res._2:::x.functionHeader)
          }
    }

    /* Sort headers */
    (objHeader.sortWith(_ < _), funHeader.sortWith(_ < _))
  }


  /**
    * Gets the object headers
    *
    * @return
    */
  def objectHeaders: List[String] = {
    val (objHeader, _) = getHeaders
    objHeader
  }


  /**
    * Get the function headers
    * @return
    */
  def functionHeaders: List[String] = {
    val (_, funHeader) = getHeaders
    funHeader
  }


  /**
    * Function that writes the output headers
    */
  private def writeHeaders(): Unit = {
    val headers = "commit,faults,path," + outputHeaders().mkString(",")
    writeFaultOutput(List(headers))
    writeFullOutput(List(headers))
  }

  /**
    * Writes to the fault output
    * @param output
    */
  def writeFaultOutput(output: List[String]) : Unit = {
    faultLock.acquire()
    faultOutput.writeOutput(output)
    faultLock.release()
  }

  /**
    * Writes to the full output
    * @param output
    */
  def writeFullOutput(output: List[String]) : Unit = {
    fullLock.acquire()
    fullOutput.writeOutput(output)
    fullLock.release()
  }

  /**
    * Closes the outputs
    */
  def closeOutputs(): Unit= {
    faultOutput.close()
    fullOutput.close()
  }


  /**
    * Function that gets all the objects in the results
    *
    * @param results the list of results
    * @return
    */
  def getResultObjects(results: List[Result]) : List[ObjectResult] = results match {
    case Nil =>
      List()
    case x::tail =>
      x match {
        case x: ObjectResult =>
          x :: getResultObjects(x.objects) ::: getResultObjects(tail)
        case x: ResultUnit =>
          getResultObjects(x.objects) ::: getResultObjects(tail)
        case _ =>
          getResultObjects(tail)
      }
  }

  /**
    * Function that gets all the files in the results
    *
    * @param results the list of results
    * @return
    */
  def getResultFiles(results: List[Result]) : List[FileResult] = results match {
    case Nil =>
      List()
    case x::tail =>
      x match {
        case x: FileResult =>
          x :: getResultFiles(tail)
        case _ =>
          getResultFiles(tail)
      }
  }

  /**
    * Function that gets all the functions in the results
    *
    * @param results the list of results
    * @return
    */
  def getResultFunctions(results: List[Result]) : List[FunctionResult] = results match {
    case Nil =>
      List()
    case x::tail =>
      x match {
        case x: FunctionResult =>
          x :: getResultFunctions(x.functions) ::: getResultFunctions(tail)
        case x: ResultUnit =>
          getResultFunctions(x.results.toList) ::: getResultFunctions(tail)
        case _ =>
          getResultFunctions(tail)
      }
  }

  /**
    * Function that returns the header length
    * @return
    */
  def headerLength: Int

  /**
    * Function to get the output headers
    */
  def outputHeaders(): List[String]

  /**
    * Function to start the metrics validation
    */
  def run(): Unit
}
