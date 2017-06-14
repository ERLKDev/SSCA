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
  def files: List[(String, String)] = {
    commitData.files.filter(x => x.status == "modified").foldLeft(List[(String, String)]())((a, b) => a ::: List((b.sha, b.filename)))
      .filter(f => """(\\src\\test\\)|(\/src\/test\/)""".r.findFirstIn(f._2).isEmpty)

  }


  /**
    * Returns a list of scala files that where changed by the commit
    * @return
    */
  def scalaFiles: List[(String, String)] = {
    commitData.files.filter(x => x.status == "modified").foldLeft(List[(String, String)]())((a, b) => a ::: List((b.sha, b.filename)))
      .filter(f => """.*\.scala$""".r.findFirstIn(f._2).isDefined).filter(f => """(\\test\\)|(\/test\/)""".r.findFirstIn(f._2).isEmpty)
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
  def getPatchData(file: String): List[(Int, Int, Int, Int)] = {
    commitData.files.find(x => x.filename == file) match {
      case Some(commitFile) =>
        commitFile.patch match {
          case Some(patchValue) =>
            val patchMatch = """@@ -((\d*),(\d*)) \+((\d*),(\d*)) @@""".r findAllMatchIn  patchValue
            patchMatch.foldLeft(List[(Int, Int, Int, Int)]()){
              (a, value) =>
                val startLineDel = value.group(2).toInt
                val stopLineDel = value.group(2).toInt + value.group(3).toInt
                val startLineAdd = value.group(5).toInt
                val stopLineAdd = value.group(5).toInt + value.group(6).toInt
                a ::: List((startLineDel, stopLineDel, startLineAdd, stopLineAdd))

            }
          case _ =>
            List()
        }
      case _ =>
        List()
    }
  }
}
