package gitcrawler

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

    println("done getting commits")
  }
}
