package ssca.validator

import codeAnalysis.analyser.result.{ObjectResult, Result, ResultUnit}
import dispatch.Http
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
    /* Create instance ids */
    val instanceIds: List[Int] = List.range(0, instances)

    val faultyClasses = instanceIds.par.map(x => runInstance(x)).foldLeft(List[String]())((a, b) => a ::: b)

    /* Initialize */
    val repo = new Repo(repoUser, repoName, repoPath + "0", branch, repoInfo)
    val analyser = new Analyser(createMetrics(), repoPath + "0", instances)

    repo.checkoutHead()
    analyser.refresh()

    val results = analyser.analyse()

    val output = processOutput(results, faultyClasses)

    writeFullOutput(output)

    closeOutputs()
    Http.shutdown()
  }


  /**
    * Runs an instance
    *
    * The instance executes a chunk of the complete payload.
    * The instance ID determines wich chunk the instance will process.
    *
    * @param id The id of the instance
    * @return
    */
  def runInstance(id: Int): List[String] = {
    val instanceRepoPath = repoPath + id
    var instanceProgress = 0

    /* Initialize instance */
    val instanceRepo = new Repo(repoUser, repoName, instanceRepoPath, branch, repoInfo)
    val instanceAnalyser = new Analyser(createMetrics(), instanceRepoPath, threads)

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

        //val output = processResults(results, x, instanceRepoPath)
        val output = getFaultyClasses(results, x, instanceRepoPath)

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


  /**
    * Gets the faulty classes
    * This function gets all the classes that contained the fault
    *
    * @param results the list of results
    * @param fault the fault
    * @param instanceRepoPath the path of the repository
    * @return
    */
  def getFaultyClasses(results: List[ResultUnit], fault: Fault, instanceRepoPath: String): List[String] = {
    getObjects(results).foldLeft(List[String]()) {
      (res, obj) =>
        /* Gets the lines that changed in the file. */
        val lines = fault.commit.getPatchData(obj.position.source.path.substring(instanceRepoPath.length + 1).replace("\\", "/"))
        if (lines.exists(patch => obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4))){
          res ::: List(obj.objectPath)
        } else {
          res
        }
    }
  }

  /**
    * Creates a CSV from the faultyClasses data and the results
    *
    * @param results the results
    * @param faultyClasses the information about faulty classes
    * @return
    */
  def processOutput(results: List[ResultUnit], faultyClasses: List[String]) : List[String] = {
    getObjects(results).foldLeft(List[String]()) {
      (out, obj) =>
        val count = faultyClasses.count(x => x == obj.objectPath)
        out ::: List("HEAD," + count + "," + obj.toCSV(headerLength) )
    }
  }

  /**
    * Function that gets all the objects in the results
    *
    * @param results the list of results
    * @return
    */
  private def getObjects(results: List[Result]) : List[ObjectResult] = results match {
    case Nil =>
      List()
    case x::tail =>
      x match {
        case x: ObjectResult =>
          x :: getObjects(x.objects) ::: getObjects(tail)
        case x: ResultUnit =>
          getObjects(x.objects) ::: getObjects(tail)
        case _ =>
          getObjects(tail)
      }
  }
}
