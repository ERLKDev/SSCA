import dispatch.{Http, as}
import dispatch.github.{GhCommit, GhCommitSummary, GhIssue, GitHub}
import dispatch._
import Defaults._
import main.scala.{Fault, Repo}
/**
  * Created by ErikL on 4/11/2017.
  */
object Main {
  implicit val formats = net.liftweb.json.DefaultFormats
  def main(args: Array[String]): Unit = {

/*    println("test")
    val req = GhIssue.get_issues("andreazevedo", "dispatch-github-specs")
    val issues = req()
    println("test2")*/
/*
    val Pattern = """\d+""".r
    val str = "Scala is Scalable 123 and cool"
    println(Pattern findAllIn str)*/

    val a = List(1, 2, 3, 4)

    val b = a.indexOf(1)
    val c = a.indexOf(5)
    val repo = new Repo("akka", "akka", "aa5065d38b6ea9e9865b176920b315ba9e63250f", List("bug", "failed", "needs-attention "))
    val faults = repo.faults

    println("done getting commits")
  }
}
