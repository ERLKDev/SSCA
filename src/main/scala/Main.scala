import analyser.result.ObjectResult
import main.scala.{Commit, Repo}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric}
import main.scala.metrics._

import scala.io.Source

/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  def main(args: Array[String]): Unit = {

    val tokenFile = Source.fromFile("github.token")
    val githubToken = tokenFile.getLines.mkString
    tokenFile.close()

    val user = "akka"
    val reponame = "akka"
    val path = "..\\tmp\\git" + user.capitalize + reponame.capitalize

    def run(id: Int, runners: Int, path: String) : Unit = {
      val repo = new Repo(user, reponame, githubToken, List("bug", "failed", "needs-attention "), path)
      println("Done loading repo: " + id)

      val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT)
      val objectMetricsHeader = metrics.filter(x => x.isInstanceOf[ObjectMetric])
        .asInstanceOf[List[ObjectMetric]].foldLeft(List[String]())((a, b) => a ::: b.objectHeader).sortWith(_ < _)

      val functionMetricsHeader = metrics.filter(x => x.isInstanceOf[FunctionMetric])
        .asInstanceOf[List[FunctionMetric]].foldLeft(List[String]())((a, b) => a ::: b.functionHeader).sortWith(_ < _)

      val header = objectMetricsHeader ::: functionMetricsHeader

      val an = new Analyser(path, metrics)
      println("Done init analyser: " + id)


      val faults = repo.faults
      val chunk = faults.grouped(faults.length / runners).toList(id - 1)
      var count = 0
      var prevCommit: Commit = null
      time {
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
                lines match {
                  case Some(patch) =>
                    y.results.foreach {
                      case obj: ObjectResult =>
                        if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                          println(obj.toCsvObjectAvr(header.length).mkString("\n"))
                        }
                    }
                  case _ =>

                }

            }
            prevCommit = x.commit
            count += 1
            println(count + "/" + chunk.length)
        }
      }
    }

    List(1).par.foreach(x => run(x, 1, path + x))
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
