package main.scala

import dispatch.github.{GhCommit, GhCommitSummary}


/**
  * Created by ErikL on 4/11/2017.
  */
class Commit(commitSummary: GhCommitSummary, repoInfo: Map[String, String]) {
  lazy val commitData: GhCommit = GhCommit.get_commit(repoInfo("user"), repoInfo("repo"), commitSummary.sha, Map("access_token" -> repoInfo("token")))()

  def message: String = commitSummary.commit.message
  def sha: String = commitSummary.sha

  def files: List[String] = {
    commitData.files.foldLeft(List[String]())((a, b) => a ::: List(b.filename))
  }

  def scalaFiles: List[String] = {
    commitData.files.foldLeft(List[String]())((a, b) => a ::: List(b.filename)).filter(f => """.*\.scala$""".r.findFirstIn(f).isDefined)
  }
}
