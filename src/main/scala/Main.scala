import main.scala.Repo

/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val repo = new Repo("akka", "akka", "73c536d54d334d411fbd12425be757f888319792", List("bug", "failed", "needs-attention "), "tmpGitDir")

  }
}
