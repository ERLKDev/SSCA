package ssca.validator

import java.io.File

import main.scala.analyser.metric.{FunctionMetric, Metric, ObjectMetric}
import ssca.validator.output.Output

import scala.concurrent.Lock
import scala.io.Source


/**
  * Created by erikl on 5/26/2017.
  */
abstract class Validator (repoPath: String, metrics: List[Metric]) {

  val token: String = loadToken()
  private val OutputDir: String = repoPath + "Output"

  createOutputDir()

  /* Creates fault and full outputs */
  private val fullOutput = new Output(OutputDir + "\\fullOutput.csv", true)
  private val faultOutput = new Output(OutputDir + "\\faultOutput.csv", true)

  /* Output locks*/
  private val faultOutputLock: Lock = new Lock
  private val fullOutputLock: Lock = new Lock
  val outputLock: Lock = new Lock

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
    * Creates the output directory
    */
  private def createOutputDir(): Unit = {
    val outputDir = new File(OutputDir)
    outputDir.mkdirs()
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
  private def metricsHeader(): (List[String], List[String]) = {
    val (objHeader, funHeader) = metrics.foldLeft((List[String](), List[String]())){
      (a, b) =>
        a match {
          case (i, k) =>
            /* If metric is both a function as an object metric */
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

    /* Sort headers */
    (objHeader.sortWith(_ < _), funHeader.sortWith(_ < _))
  }

  /**
    * Gets the object headers
    *
    * @return
    */
  def objectHeaders: List[String] = {
    val (objHeader, _) = metricsHeader()
    objHeader
  }

  /**
    * Get the function headers
    * @return
    */
  def functionHeaders: List[String] = {
    val (_, funHeader) = metricsHeader()
    funHeader
  }

  /**
    * Writes the headers to a file
    */
  def writeObjectHeaders(): Unit = {
    val funHeader = functionHeaders.map("functionAvr" + _.capitalize) ::: functionHeaders.map("functionSum" + _.capitalize) ::: functionHeaders.map("functionMax" + _.capitalize)
    val header = objectHeaders:::funHeader

    fullOutput.writeOutput(List("commit,faults,path," + header.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path," + header.mkString(",")))
  }

  /**
    * Writes the headers to a file
    */
  def writeFunctionHeaders(): Unit = {
    fullOutput.writeOutput(List("commit,faults,path," + functionHeaders.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path," + functionHeaders.mkString(",")))
  }

  /**
    * Writes the headers to a file
    */
  def writeFileHeaders(): Unit = {
    val funHeader = functionHeaders.map("functionAvr" + _.capitalize) ::: functionHeaders.map("functionSum" + _.capitalize) ::: functionHeaders.map("functionMax" + _.capitalize)
    val objHeader = objectHeaders.map("objectAvr" + _.capitalize) ::: objectHeaders.map("objectSum" + _.capitalize) ::: objectHeaders.map("objectMax" + _.capitalize)
    val header = objHeader//:::funHeader

    fullOutput.writeOutput(List("commit,faults,path," + header.mkString(",")))
    faultOutput.writeOutput(List("commit,faults,path," + header.mkString(",")))
  }

  /**
    * Function that returns the header length
    * @return
    */
  def headerLength: Int

  /**
    * Writes to the fault output
    * @param output
    */
  def writeFaultOutput(output: List[String]) : Unit = {
    faultOutputLock.acquire()
    faultOutput.writeOutput(output)
    faultOutputLock.release()
  }

  /**
    * Writes to the full output
    * @param output
    */
  def writeFullOutput(output: List[String]) : Unit = {
    fullOutputLock.acquire()
    fullOutput.writeOutput(output)
    fullOutputLock.release()
  }

  /**
    * Closes the outputs
    */
  def closeOutputs(): Unit= {
    faultOutput.close()
    fullOutput.close()
  }

  /**
    * Function to run the validator
    */
  def run(): Unit
}
