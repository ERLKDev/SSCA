package ssca.validator

import codeAnalysis.analyser.result.ResultUnit
import dispatch.Http
import gitCrawler.{Fault, Repo, RepoInfo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 6/1/2017.
  */
class ValidatorOFile(path: String, repoUser: String, repoName: String, branch: String, labels: List[String], instances: Int, threads: Int, metrics: List[Metric])
  extends Validator(path, repoUser, repoName, branch, labels, instances, threads, metrics){

  /**
    * Function that returns the header length
    *
    * @return
    */
  override def headerLength: Int = 3 * objectHeaders.length + 3 * functionHeaders.length

  /**
    * Function to get the output headers
    */
  override def outputHeaders(): List[String] = objectHeaders.map("objectAvr" + _.capitalize) :::
    objectHeaders.map("objectSum" + _.capitalize) ::: objectHeaders.map("objectMax" + _.capitalize) :::
    functionHeaders.map("functionAvr" + _.capitalize) ::: functionHeaders.map("functionSum" + _.capitalize) :::
    functionHeaders.map("functionMax" + _.capitalize)


/**
  * Gets the faulty classes
  * This function gets all the classes that contained the fault
  *
  * @param results the list of results
  * @param fault the fault
  * @param instanceRepoPath the path of the repository
  * @return
  */
  def getFaultyUnits(results: List[ResultUnit], fault: Fault, instanceRepoPath: String): List[String] = {
    getResultFiles(results).foldLeft(List[String]()) {
      (res, file) =>
        /* Gets the lines that changed in the file. */
        val lines = fault.commit.getPatchData(file.position.source.path.substring(instanceRepoPath.length + 1).replace("\\", "/"))
        if (lines.exists(patch => file.includes(patch._1, patch._2) || file.includes(patch._3, patch._4))){
          res ::: List(file.filePath)
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
    getResultFiles(results).foldLeft(List[String]()) {
      (out, file) =>
        val count = faultyUnits.count(x => x == file.filePath)
        out ::: List("HEAD," + count + "," + file.toCSV(headerLength) )
    }
  }
}
