package main.scala

import dispatch.github.{GhCommit, GhCommitSummary}


/**
  * Created by ErikL on 4/11/2017.
  */
class Commit(commitSummary: GhCommitSummary, repoInfo: Map[String, String]) {
  lazy val commitData: GhCommit = GhCommit.get_commit(repoInfo("user"), repoInfo("repo"), commitSummary.sha, Map("access_token" -> repoInfo("token")))()

  def message: String = commitSummary.commit.message
  def sha: String = commitSummary.sha

  /**
    * Returns a list of files that where changed by the commit
    * @return
    */
  def files: List[String] = {
    commitData.files.filter(x => x.status == "modified").foldLeft(List[String]())((a, b) => a ::: List(b.filename))
  }


  /**
    * Returns a list of scala files that where changed by the commit
    * @return
    */
  def scalaFiles: List[String] = {
    commitData.files.filter(x => x.status == "modified").foldLeft(List[String]())((a, b) => a ::: List(b.filename)).filter(f => """.*\.scala$""".r.findFirstIn(f).isDefined)
  }


  /**
    * Function to get the patch(change) data of a commit
    *
    * @param file The file name of a changed file
    * @return
    */
  def getPatchData(file: String): Option[(Int, Int, Int, Int)] = {
    commitData.files.find(x => x.filename == file) match {
      case Some(commitFile) =>
        commitFile.patch match {
          case Some(patchValue) =>
            val patchMatch = """@@ -((\d*),(\d*)) \+((\d*),(\d*)) @@""".r findFirstMatchIn patchValue

            patchMatch match {
              case Some(value) =>
                val startLineDel = value.group(2).toInt
                val stopLineDel = value.group(2).toInt + value.group(3).toInt
                val startLineAdd = value.group(5).toInt
                val stopLineAdd = value.group(5).toInt + value.group(6).toInt
                Some(startLineDel, stopLineDel, startLineAdd, stopLineAdd)
              case _ =>
                None
            }
          case _ =>
            None
        }
      case _ =>
        None
    }
  }
}
