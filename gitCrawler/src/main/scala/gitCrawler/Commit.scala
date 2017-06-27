package gitCrawler

import java.util.Date

import dispatch.github.{GhCommit, GhCommitSummary}
import gitCrawler.util.GitDataBase


/**
  * Created by ErikL on 4/11/2017.
  */
class Commit(commitSummary: GhCommitSummary, info: Map[String, String], data: GhCommit) {
  val dataBase = new GitDataBase(info("repoPath"))

  def commitData: GhCommit = {
    if (data == null)
      dataBase.readCommit(sha) match {
        case Some(commit) =>
          return commit
        case _ =>
          val result = GhCommit.get_commit(info("user"), info("repo"), commitSummary.sha, info("token"))()
          dataBase.writeCommit(result)
          return result
      }
    data
  }

  def message: String = commitSummary.commit.message
  def sha: String = commitSummary.sha

  /**
    * Returns a list of files that where changed by the commit
    * @return
    */
  def files: List[String] = {
    commitData.files.filter(x => x.status == "modified").foldLeft(List[String]())((a, b) => a ::: List(b.filename))
      .filter(f => """(\\src\\test\\)|(\/src\/test\/)""".r.findFirstIn(f).isEmpty)
  }


  /**
    * Returns a list of scala files that where changed by the commit
    * @return
    */
  def scalaFiles: List[String] = {
    commitData.files.filter(x => x.status == "modified").foldLeft(List[String]())((a, b) => a ::: List(b.filename))
      .filter(f => """.*\.scala$""".r.findFirstIn(f).isDefined).filter(f => """(\\test\\)|(\/test\/)""".r.findFirstIn(f).isEmpty)
  }


  /**
    * Returns the date of the commit
    * @return
    */
  def date: Date = {
    commitSummary.commit.author.date
  }


  /**
    * Checks whether the commit is committed between two dates
    * @param from the from date
    * @param until the until date
    * @return
    */
  def isBetween(from: Date, until: Date): Boolean = {
    commitSummary.commit.author.date.before(until) && commitSummary.commit.author.date.after(from)
  }


  /**
    * Function to get the patch(change) data of a commit
    *
    * @param file The file name of a changed file
    * @return
    */
  def getPatchData(file: String): List[Int] = {
    commitData.files.find(x => x.filename == file) match {
      case Some(commitFile) =>
        commitFile.patch match {
          case Some(patchValue) =>

            val headers = ("""@@ -((\d*),(\d*)) \+((\d*),(\d*)) @@""".r findAllIn patchValue).toList
            val patchMatches = headers.zip(patchValue.split("""@@ -((\d*),(\d*)) \+((\d*),(\d*)) @@""").filter(x => x != "")).map(x => x._1 + x._2)
            patchMatches.foldLeft(List[Int]()){
              (a, b) =>
                val patchMatch = """@@ -((\d*),(\d*)) \+((\d*),(\d*)) @@((.|\n)*)""".r findFirstMatchIn b
                val res = patchMatch match {
                  case Some(value) =>
                    val startLineDel = value.group(2).toInt
                    val code = value.group(7)
                    val lines = code.split("\n")

                    linesToRows(lines.toList, startLineDel, startLineDel)
                  case None =>
                    List()
                }
                a ::: res
            }
          case _ =>
            List()
        }
      case _ =>
        List()
    }
  }

  def linesToRows(lines: List[String], n: Int, k: Int): List[Int] = lines match {
    case Nil =>
      List()
    case x::tail =>
      if (x.startsWith("-"))
        n :: linesToRows(tail, n + 1, k)
      else if (x.startsWith("+"))
        k :: linesToRows(tail, n, k)
      else
        linesToRows(tail, n + 1, n + 1)
  }
}
