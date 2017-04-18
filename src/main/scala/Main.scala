import main.scala.Repo
import main.scala.analyser.Analyser
import main.scala.analyser.result.{UnitResult, UnitType}
import main.scala.metrics._

/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val repo = new Repo("akka", "akka", "73c536d54d334d411fbd12425be757f888319792", List("bug", "failed", "needs-attention "), "..\\tmpGitDir2")
    println("Done loading repo")

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT, new NOC)
    val an = new Analyser("..\\tmpGitDir2", metrics)
    println("Done init analyser")


    val faults = repo.faults
    var count = 0
    faults.foreach{
      x =>
        repo.checkoutPreviousCommit(x.commit)
        an.refresh()
        x.commit.files.foreach{
          y =>
            val lines = x.commit.getPatchData(y)
            val result = an.analyse("..\\tmpGitDir2\\" + y)


            lines match {
              case Some(patch) =>
                result match {
                  case res: UnitResult =>
                    res.results.foreach {
                      case obj: UnitResult =>
                        if (obj.unitType == UnitType.Object) {
                          if (obj.includes(patch._1, patch._2) || obj.includes(patch._3, patch._4)) {
                            println(obj)
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
