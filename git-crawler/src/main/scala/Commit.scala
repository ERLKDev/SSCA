package main.scala

import dispatch.github.{GhCommit, GhCommitSummary}

/**
  * Created by ErikL on 4/11/2017.
  */
class Commit(commitSummary: GhCommitSummary, issueNumbers: List[Int], repoInfo: Map[String, String]) {
  lazy val commitData: GhCommit = GhCommit.get_commit(repoInfo.get("user").toString, repoInfo.get("repo").toString, commitSummary.sha, repoInfo.get("token").toString)()

  def getFiles: List[String] = {
    commitData.files.foldLeft(List[String]())((a, b) => a ::: List(b.filename))
  }
}
