import java.io.File

import main.scala.Repo

import scala.io.Source
/**
  * Created by ErikL on 4/11/2017.
  */
object Main {
  implicit val formats = net.liftweb.json.DefaultFormats

  def main(args: Array[String]): Unit = {

    val tokenFile = Source.fromFile("github.token")
    val githubToken = tokenFile.getLines.mkString
    tokenFile.close()

    val user = "akka"
    val reponame = "akka"
    val path = "..\\tmp\\git" + user.capitalize + reponame.capitalize

    val repo = new Repo(user, reponame, githubToken, List("bug", "failed", "needs-attention "), path + "1")
    println("Repo Loaded!")
    println(repo.changedFiles(repo.commits(0), repo.commits(2)))

/*    println("checkout 1")
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
    repo.checkoutPreviousCommit(repo.faults(2).commit)*/



    println("done getting commits")
  }
}
