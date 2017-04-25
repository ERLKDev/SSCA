import java.io.File

import analyser.result.ObjectResult
import gitCrawler.{Commit, Repo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric}
import main.scala.metrics._

import scala.concurrent.Lock
import scala.io.Source

/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  def main(args: Array[String]): Unit = {

    val tokenFile = Source.fromFile("github.token")
    val githubToken = tokenFile.getLines.mkString
    tokenFile.close()

    val user = "shadowsocks"
    val reponame = "shadowsocks-android"
    val path = "..\\tmp\\git" + user.capitalize + reponame.capitalize

    val fullOutput = path + "Output\\fullOutput.csv"
    val faultOutput = path + "Output\\faultOutput.csv"

    val outputDir = new File(path + "Output")
    outputDir.mkdirs()

    val fullOutputFile = new File(fullOutput)
    fullOutputFile.delete()
    fullOutputFile.createNewFile()

    val faultOutputFile = new File(faultOutput)
    faultOutputFile.delete()
    faultOutputFile.createNewFile()

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT)

    val objectMetricsHeader = metrics.filter(x => x.isInstanceOf[ObjectMetric])
        .asInstanceOf[List[ObjectMetric]].foldLeft(List[String]())((a, b) => a ::: b.objectHeader).sortWith(_ < _)

    val functionMetricsHeader = metrics.filter(x => x.isInstanceOf[FunctionMetric])
      .asInstanceOf[List[FunctionMetric]].foldLeft(List[String]())((a, b) => a ::: b.functionHeader).sortWith(_ < _)

    val header = objectMetricsHeader ::: functionMetricsHeader

    Output.writeOutput(List("commit, faults, path, " + header.mkString(", ")), fullOutput)
    Output.writeOutput(List("commit, faults, path, " + header.mkString(", ")), faultOutput)


    val lock = new Lock

    def run(id: Int, runners: Int, path: String) : Unit = {
      val repo = new Repo(user, reponame, githubToken, List("bug", "failed", "needs-attention "), path)
      println("Done loading repo: " + id)

      val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT)

      val an = new Analyser(metrics, path, 4)
      println("Done init analyser: " + id)


      val faults = repo.faults
      val chunk = faults.grouped(math.ceil(faults.length.toDouble / runners).toInt).toList(id - 1)
      var count = 0


      var prevCommit: Commit = null

      chunk.foreach {
        x =>
          repo.checkoutPreviousCommit(x.commit)
          an.refresh()

          val results = if (prevCommit != null) {
            val files = repo.changedFiles(prevCommit, x.commit).map(x => path + "\\" + x)
            an.analyse(files)
          } else {
            an.analyse()
          }

          results.foreach {
          y =>
            val lines = x.commit.getPatchData(y.position.source.path.substring(path.length + 1).replace("\\", "/"))
            y.results.foreach {
              case obj: ObjectResult =>
                lines match {
                  case Some(patch) =>
                    lock.acquire()
                    if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                      Output.writeOutput(obj.toCsvObjectAvr(header.length).map(x.commit.sha + ", " + x.issues.length + ", " + _),  faultOutput)
                      Output.writeOutput(obj.toCsvObjectAvr(header.length).map(x.commit.sha + ", " + x.issues.length + ", " + _), fullOutput)
                      //println(obj.toCsvObjectAvr(header.length).map(x.commit.sha + ", " + x.issues.length + ", " + _).mkString("\n"))
                    }else{
                      Output.writeOutput(obj.toCsvObjectAvr(header.length).map(x.commit.sha + ", " + 0 + ", " + _), fullOutput)
                    }
                    lock.release()
                  case _ =>
                    lock.acquire()
                    Output.writeOutput(obj.toCsvObjectAvr(header.length).map(x.commit.sha + ", " + 0 + ", " + _), fullOutput)
                    lock.release()
                }
            }
          }
          prevCommit = x.commit
          count += 1
          println(count + "/" + chunk.length + ":  " + results.length + " => " + id)
      }
    }


    List(1, 2, 3).par.foreach(x => run(x, 3, path + x))

    //run(1, 1, path + 1)

    println("Done")
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
  }
}
