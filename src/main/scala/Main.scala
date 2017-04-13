import main.scala.Repo
import main.scala.analyser.Analyser
import main.scala.metrics._

/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val repo = new Repo("akka", "akka", "73c536d54d334d411fbd12425be757f888319792", List("bug", "failed", "needs-attention "), "..\\tmpGitDir")
    println("Done loading repo")

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT)
    val an = new Analyser("..\\tmpGitDir", metrics)
    println("Done init analyser")

    repo.faults.foreach{
      x =>
        repo.checkoutPreviousCommit(x.commit)
        x.commit.files.foreach{
          y =>
            println(an.analyse("..\\tmpGitDir\\" + y))
        }
    }

  }
}
