package main.scala

import dispatch.github.{GhCommit, GhCommitSummary, GhIssue, GhIssueSummary}

/**
  * Created by ErikL on 4/11/2017.
  */
class Repo(userName: String, repoName: String, token: String) {

  val repoInfo = Map("user" -> userName, "repo" -> repoName, "token" -> token)

  lazy val issueCommits: List[Commit] = getIssueCommits
  lazy val issuesNumbers: List[Int] = getIssueNumbers

  private def getIssueCommits: List[Commit] = {
    def recursive(page: Int) : List[Commit] = {
      val commitsRes = GhCommit.get_commits(userName, repoName,  Map("page" -> page.toString, "per_page" -> "100", "access_token" -> token))()
      val commits = commitsRes.foldLeft(List[Commit]()){
        (a, b) =>
          if (isIssue(b))
            a ::: List(new Commit(b, getIssueNumbers(b), repoInfo))
          else
            a
      }
      if (commits.nonEmpty)
        commits ::: recursive(page + 1)
      else
        commits
    }
    recursive(1)
  }

  private def isIssue(commitSummary: GhCommitSummary) : Boolean = {
    val pattern = """(?i)(clos(e[sd]?|ing)|fix(e[sd]|ing)?|resolv(e[sd]?)|#(\d+))""".r

    if ((pattern findAllIn commitSummary.commit.message).isEmpty)
      return false

    if (getIssueNumbers(commitSummary).isEmpty)
      return false
    true
  }

  private def getIssueNumbers(commitSummary: GhCommitSummary) : List[Int] = {
    val pattern2 = """#(\d+)""".r
    val possibleNumbers = pattern2 findAllIn commitSummary.commit.message
    if (possibleNumbers.isEmpty)
      return List[Int]()
    val ints = possibleNumbers.matchData.map(x => x.group(1).toInt).toList
    val b = ints.filter(x => issuesNumbers.contains(x))
    val c = issuesNumbers.contains(ints(0))
    b
  }

  private def getIssueNumbers: List[Int] = {
    def recursive(page: Int, label: String) : List[Int] = {
      val issuesRes = GhIssue.get_issues(userName, repoName,  Map("page" -> page.toString, "per_page" -> "100", "access_token" -> token, "state" -> "all", "labels" -> label))()
      val issueNumbers = issuesRes.foldLeft(List[Int]())((a, b) => a ::: List(b.number))
      if (issueNumbers.nonEmpty)
        issueNumbers ::: recursive(page + 1, label)
      else
        issueNumbers
    }
    recursive(1, "bug") ::: recursive(1, "failed") ::: recursive(1, "needs-attention")
  }

}
