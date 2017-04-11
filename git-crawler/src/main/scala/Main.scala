import java.io.File

import main.scala.Repo
/**
  * Created by ErikL on 4/11/2017.
  */
object Main {
  implicit val formats = net.liftweb.json.DefaultFormats

  def main(args: Array[String]): Unit = {

    val repo = new Repo("akka", "akka", "aa5065d38b6ea9e9865b176920b315ba9e63250f", List("bug", "failed", "needs-attention "), "tmpGitDir")

    println("checkout 1")
    repo.faults(0).commit
    repo.checkoutCommit(repo.faults(0).commit)

    println("checkout 2")
    repo.faults(1).commit
    repo.checkoutCommit(repo.faults(1).commit)

    println("checkout 3")
    repo.faults(2).commit
    repo.checkoutCommit(repo.faults(2).commit)

    println("checkout 1 prev")
    repo.faults(0).commit
    repo.checkoutPreviousCommit(repo.faults(0).commit)

    println("checkout 2 prev")
    repo.faults(1).commit
    repo.checkoutPreviousCommit(repo.faults(1).commit)

    println("checkout 3 prev")
    repo.faults(2).commit
    repo.checkoutPreviousCommit(repo.faults(2).commit)



    println("done getting commits")
  }
}
