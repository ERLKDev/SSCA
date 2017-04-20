import analyser.result.ObjectResult
import main.scala.Repo
import main.scala.analyser.Analyser
import main.scala.analyser.prerun.PreRunJob
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

    val repo = new Repo(user, reponame, githubToken, List("bug", "failed", "needs-attention "), path)
    println("Done loading repo")

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT)
    val an = new Analyser(path, metrics)
    println("Done init analyser")


    val faults = repo.faults
    var count = 0
    faults.foreach{
      x =>
        repo.checkoutPreviousCommit(x.commit)
        an.refresh()
        val files = x.commit.files.map(x => path + "\\" + x)
        val results = an.analyse(files)
        results.foreach{
          y =>
            val lines = x.commit.getPatchData(y.position.source.path.substring(path.length + 1).replace("\\", "/"))
            lines match {
              case Some(patch) =>
                  y.results.foreach {
                    case obj: ObjectResult =>
                      if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                        println(obj.toCsvObjectAvr.mkString("\n"))
                      }
                  }
              case _ =>

            }

        }
        count += 1
        println(count + "/" + faults.length)
    }
    println("Done")
  }
}
