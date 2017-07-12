package ssca.validator

import codeAnalysis.analyser.result.ResultUnit
import gitCrawler.Fault
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 6/1/2017.
  */
class ValidatorNObject(path: String, repoUser: String, repoName: String, branch: String, labels: List[String], instances: Int, threads: Int, metrics: List[Metric], outputName: String = "fullOutput")
  extends ValidatorN(path, repoUser, repoName, branch, labels, instances, threads, metrics, outputName){

  /**
    * Function that returns the header length
    *
    * @return
    */
  override def headerLength: Int = objectHeaders.length + 3 * functionHeaders.length

  /**
    * Function to get the output headers
    */
  override def outputHeaders(): List[String] = objectHeaders ::: functionHeaders.map("functionAvr" + _.capitalize) :::
    functionHeaders.map("functionSum" + _.capitalize) ::: functionHeaders.map("functionMax" + _.capitalize)


  /**
    * Gets the current units
    * This function gets all the units of the head version
    *
    * @param results the list of results
    * @return
    */
  override def getCurrentUnits(results: List[ResultUnit]): List[String] = {
    getResultObjects(results).foldLeft(List[String]()) {
      (out, obj) =>
        out ::: List(obj.objectPath)
    }
  }

  /**
    * Gets the faulty classes
    * This function gets all the classes that contained the fault
    *
    * @param results the list of results
    * @param fault the fault
    * @param instanceRepoPath the path of the repository
    * @return
    */
  def getFaultyUnits(results: List[ResultUnit], currentUnits: List[String], fault: Fault, instanceRepoPath: String): List[String] = {
    getResultObjects(results).foldLeft(List[String]()) {
      (res, obj) =>
        if (currentUnits.contains(obj.objectPath)) {
          /* Gets the lines that changed in the file. */
          val lines = fault.commit.getPatchData(obj.position.source.path.substring(instanceRepoPath.length + 1).replace("\\", "/"))
          if (obj.includesPatch(lines)) {
            writeFullOutput(List("HEAD,1," + obj.toCSV(headerLength)))
            writeFaultOutput(List("HEAD,1," + obj.toCSV(headerLength)))
            res ::: List(obj.objectPath)
          } else {
            res
          }
        } else {
          res
        }
    }
  }

  /**
    * Creates a CSV from the faultyUnits data and the results
    *
    * @param results the results
    * @param faultyUnits the information about faulty units
    * @return
    */
  def processOutput(results: List[ResultUnit], faultyUnits: List[String]) : List[String] = {
    getResultObjects(results).foldLeft(List[String]()) {
      (out, obj) =>
        if (!faultyUnits.contains(obj.objectPath))
          out ::: List("HEAD,0," + obj.toCSV(headerLength))
        else
          out
    }
  }

}
