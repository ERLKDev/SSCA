package main.scala

import dispatch.github.{GhCommit, GhCommitSummary}

/**
  * Created by ErikL on 4/11/2017.
  */
class Commit(commitSummary: GhCommitSummary, repoInfo: Map[String, String]) {
  lazy val commitData: GhCommit = GhCommit.get_commit(repoInfo.get("user").toString, repoInfo.get("repo").toString, commitSummary.sha, repoInfo.get("token").toString)()

  def message: String = commitSummary.commit.message

  def files: List[String] = {
    commitData.files.foldLeft(List[String]())((a, b) => a ::: List(b.filename))
  }
}
