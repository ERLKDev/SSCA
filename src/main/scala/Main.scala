import main.scala.Repo
import main.scala.analyser.Analyser
import main.scala.analyser.prerun.PreRunJob
import main.scala.analyser.result.{UnitResult, UnitType}
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

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT, new NOC)
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
        results.asInstanceOf[UnitResult].getResults.foreach {
          y =>
            val lines = x.commit.getPatchData(y.asInstanceOf[UnitResult].position.source.path)

            lines match {
              case Some(patch) =>
                y match {
                  case res: UnitResult =>
                    res.results.foreach {
                      case obj: UnitResult =>
                        if (obj.unitType == UnitType.Object) {
                          if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                            //println(obj)
                          }
                        }
                    }
                  case _ =>

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
