package ssca.validator

import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import dispatch.Http
import gitCrawler.{Fault, Repo, RepoInfo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 4/25/2017.
  */
class ValidatorN(repoUser: String, repoName: String, repoPath: String, instances: Int,
                 instanceThreads: Int, metrics: List[Metric], labels: List[String], branch: String)
  extends Validator(repoPath, metrics){


  private val instanceIds: List[Int] = List.range(0, instances)

  private val OutputDir = repoPath + "Output"

  private var totalCount = 0
  private var outputLines = 0

  def run() : Unit = {
    writeObjectHeaders()
    totalCount = 0

    val repoInfo = new RepoInfo(repoUser, repoName, token, labels, "master", repoPath)
    val faultyClassesTmp = instanceIds.par.map(runInstance(_, repoInfo)).foldLeft(List[String]())(_ ::: _)
    val faultyClasses = faultyClassesTmp.map(x => x.replaceAll(repoPath.replace("\\", "\\\\") + """\d""", repoPath))

    println("Last phase")
    println("Start init repo last phase")
    val repo = new Repo(repoUser, repoName, repoPath + 0, branch, repoInfo)
    println("Done loading repo last phase")

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), repoPath + 0, instanceThreads)
    println("Done init analyser last phase")

    println("analyse")
    val results = an.analyse()

    val output = objectOutput2(faultyClasses, results)

    println(results.length + "    " + output.length)
    writeFullOutput(output)

    outputLock.acquire()
    outputLines += output.length
    outputLock.release()

    closeOutputs()
    Http.shutdown()

    println("Total output lines: " + outputLines)
  }

  private def runInstance(id: Int, repoInfo: RepoInfo): List[String] = {
    val instancePath = repoPath + id

    /* Init the repo for the instance */
    println("Start init repo: " + id)
    val repo = new Repo(repoUser, repoName, instancePath, branch, repoInfo)
    println("Done loading repo: " + id)

    /* Init the analyser for the instance */
    val an = new Analyser(createMetrics(), instancePath, instanceThreads)
    println("Done init analyser: " + id)

    /* Get the faults and select the correct chunk. */
    val faults = repoInfo.faults
    val chunk = faults.grouped(math.ceil(faults.length.toDouble / instances).toInt).toList(id)

    var count = 0

    println("Start => " + id)
    /* Analyse each fault. */
    val faultyFiles = chunk.foldLeft(List[String]()) {
      (a, x) =>
        /* Commit to previous commit. */
        repo.checkoutPreviousCommit(x.commit)
        an.refresh()


        /* Get the result. */
        val results = an.analyse(x.commit.scalaFiles.map(instancePath + "\\" + _))

        /* Run output function. */
        val output = objectOutput(instancePath, x, results)

        count += 1

        if (output._1.nonEmpty)
          writeFaultOutput(output._1)
        if (output._2.nonEmpty)
          writeFullOutput(output._2)


        outputLock.acquire()
        totalCount += 1
        outputLines += output._2.length
        outputLock.release()

        val nextSha = {
          val index = chunk.indexOf(x) + 1
          if (index < chunk.length)
            chunk(index).commit.sha
          else
            "Last"
        }

        println(id + ":\t" + count + "/" + chunk.length + "(" + (count * 100) / chunk.length + "%)\t\tTotal: "
          + totalCount + "/" + faults.length + "(" + (totalCount * 100) / faults.length + "%)\t\t"
          + results.length + "\t\t=>\t" + x.commit.sha + "\t->\t" + nextSha)

        x.unload()
        a ::: output._3
    }

    println(id + " Done!")
    an.close()
    faultyFiles
  }


  def objectOutput(instancePath: String, fault: Fault, results: List[ResultUnit]): (List[String], List[String], List[String]) = {
    def recursive(result: ResultUnit) : (List[String], List[String], List[String]) =  {
      val lines = fault.commit.getPatchData(result.position.source.path.substring(instancePath.length + 1).replace("\\", "/"))
      result.results.foldLeft((List[String](), List[String](), List[String]())) {
        (a, b) =>
          b match {
            case obj: ObjectResult =>
              lines match {
                case Some(patch) =>
                  if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                    val out = recursive(obj)
                    (a._1 ::: List(fault.commit.sha + "," + 1 + "," + obj.toCSV(headerLength)) ::: out._1,
                      a._2 ::: List(fault.commit.sha + "," + 1 + "," + obj.toCSV(headerLength))::: out._2, obj.objectPath :: a._3 ::: out._3)
                  } else {
                    recursive(obj)
                  }
                case _ =>
                  recursive(obj)
              }
            case unit: ResultUnit =>
              recursive(unit)
            case _ =>
              a
          }
      }
    }

    results.foldLeft((List[String](), List[String](), List[String]())) {
      (r, y) =>
        val res = recursive(y)
        (r._1 ::: res._1, r._2 ::: res._2, r._3 ::: res._3)
    }
  }

  def objectOutput2(faultyClasses: List[String], results: List[ResultUnit]): List[String] = {

    def recursive(result: ResultUnit) : List[String] =  {
      result.results.foldLeft(List[String]()) {
        (a, b) =>
          b match {
            case obj: ObjectResult =>
              if (faultyClasses.contains(obj.objectPath)) {
                a ::: recursive(obj)
              } else {
                a ::: List("HEAD," + 0 + "," + obj.toCSV(headerLength)) ::: recursive(obj)
              }
            case unit: ResultUnit =>
              a ::: recursive(unit)

            case _ =>
              a
          }
      }
    }

    results.foldLeft(List[String]()) {
      (r, y) =>
        r ::: recursive(y)
    }
  }


  override def headerLength: Int = {
    objectHeaders.length + functionHeaders.length * 3
  }
}
