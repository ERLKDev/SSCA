package ssca.validator

import codeAnalysis.analyser.result.{ObjectResult, Result, ResultUnit}
import gitCrawler.{Fault, Repo, RepoInfo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 6/1/2017.
  */
class NewValidatorO(path: String, repoUser: String, repoName: String, branch: String, labels: List[String], instances: Int, threads: Int, metrics: List[Metric])
  extends NewValidator(path, repoUser, repoName, metrics){

  private val repoInfo = new RepoInfo(repoUser, repoName, token, labels, branch, repoPath)
  private var progress = 0

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
    * Function to start the metrics validation
    */
  override def run(): Unit = {

  }



  def runInstance(id: Int): List[String] = {
    val instanceRepoPath = repoPath + id
    var instanceProgress = 0

    /* Initialize instance */
    val instanceRepo = new Repo(repoUser, repoName, instanceRepoPath, branch, repoInfo)
    val instanceAnalyser = new Analyser(metrics, instanceRepoPath, threads)

    println("Instance " + id + " Initialized")

    /* Checks if there are faults to process for this instance. */
    val faults = repoInfo.faults
    if (faults.length <= id)
      return List()

    /* Gets the chunk the instance should process */
    val chunk = faults.grouped(math.ceil(faults.length.toDouble / instances).toInt).toList(id)

    /* Analyse each fault */
    val result = chunk.foldLeft(List[String]()) {
      (r, x) =>

        /* Commit to previous commit. */
        instanceRepo.checkoutPreviousCommit(x.commit)
        instanceAnalyser.refresh()

        /* Get the result. */
        val results = instanceAnalyser.analyse(x.commit.files.map(instanceRepoPath + "\\" + _))

        val output = processResults(results, x, instanceRepoPath)

        val nextSha = {
          val index = chunk.indexOf(x) + 1
          if (index < chunk.length)
            chunk(index).commit.sha
          else
            "Last"
        }

        /* Handles the output and progress */
        consoleLock.acquire()

        progress += 1
        instanceProgress += 1

        println(id + ":\t" + instanceProgress + "/" + chunk.length + "(" + (instanceProgress * 100) / chunk.length + "%)\t\tTotal: "
          + progress + "/" + faults.length + "(" + (progress * 100) / faults.length + "%)\t\t"
          + results.length + "\t\t=>\t" + x.commit.sha + "\t->\t" + nextSha)

        consoleLock.release()

        x.unload()
        r ::: output
    }

    instanceAnalyser.close()
    println("Instance " + id + " Done!")
    result
  }


  def processResults(results: List[ResultUnit], fault: Fault, instanceRepoPath: String): List[String] = {
    /* Gets all the object results from the results. */
    def getObjects(remain: List[Result]) : List[ObjectResult] = remain match {
      case Nil =>
        List()
      case x::tail =>
        x match {
          case x: ObjectResult =>
            x :: getObjects(x.results.toList) ::: getObjects(tail)
          case x: ResultUnit =>
            getObjects(x.results.toList) ::: getObjects(tail)
          case _ =>
            getObjects(tail)
        }
    }

    getObjects(results).foldLeft(List[String]()) {
      (res, obj) =>
        /* Gets the lines that changed in the file. */
        val lines = fault.commit.getPatchData(obj.position.source.path.substring(instanceRepoPath.length + 1).replace("\\", "/"))
        List()
/*        lines match {
          case Some(patch) =>
            if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
              res ::: List(obj.objectPath)
            } else {
              List()
            }
          case None =>
            List()
        }*/
    }
  }
}
