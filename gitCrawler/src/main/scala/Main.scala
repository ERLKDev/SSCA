import java.io.File

import main.scala.Repo
/**
  * Created by ErikL on 4/11/2017.
  */
object Main {
  implicit val formats = net.liftweb.json.DefaultFormats

  def main(args: Array[String]): Unit = {

    val repo = new Repo("akka", "akka", "73c536d54d334d411fbd12425be757f888319792", List("bug", "failed", "needs-attention "), "tmpGitDir")

    println("checkout 1")
    repo.checkoutCommit(repo.faults(0).commit)

    println("checkout 2")
    repo.checkoutCommit(repo.faults(1).commit)

    println("checkout 3")
    repo.checkoutCommit(repo.faults(2).commit)

    println("checkout 1 prev")
    repo.checkoutPreviousCommit(repo.faults(0).commit)

    println("checkout 2 prev")
    repo.checkoutPreviousCommit(repo.faults(1).commit)

    println("checkout 3 prev")
    repo.checkoutPreviousCommit(repo.faults(2).commit)



    println("done getting commits")
  }
}
