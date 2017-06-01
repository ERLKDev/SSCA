package ssca.validator

import codeAnalysis.analyser.result.ResultUnit
import dispatch.Http
import gitCrawler.{Fault, Repo, RepoInfo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 6/1/2017.
  */
abstract class NewValidatorO(path: String, repoUser: String, repoName: String, branch: String, labels: List[String], instances: Int, threads: Int, metrics: List[Metric])
  extends NewValidator(path, repoUser, repoName, metrics){

  private val repoInfo = new RepoInfo(repoUser, repoName, token, labels, branch, repoPath)
  private var progress = 0

  /**
    * Function to start the metrics validation
    */
  override def run(): Unit = {
    /* Create instance ids */
    val instanceIds: List[Int] = List.range(0, instances)

    val faultyUnits = instanceIds.par.map(x => runInstance(x)).foldLeft(List[String]())((a, b) => a ::: b)

    /* Initialize */
    val repo = new Repo(repoUser, repoName, repoPath + "0", branch, repoInfo)
    val analyser = new Analyser(createMetrics(), repoPath + "0", instances)

    repo.checkoutHead()
    analyser.refresh()

    val results = analyser.analyse()

    val output = processOutput(results, faultyUnits)

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
        val output = getFaultyUnits(results, x, instanceRepoPath)

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
    * Gets the faulty units
    * This function gets all the units that contained the fault
    *
    * @param results the list of results
    * @param fault the fault
    * @param instanceRepoPath the path of the repository
    * @return
    */
  def getFaultyUnits(results: List[ResultUnit], fault: Fault, instanceRepoPath: String): List[String]


  /**
    * Creates a CSV from the faultyUnits data and the results
    *
    * @param results the results
    * @param faultyUnits the information about faulty units
    * @return
    */
  def processOutput(results: List[ResultUnit], faultyUnits: List[String]) : List[String]
}
