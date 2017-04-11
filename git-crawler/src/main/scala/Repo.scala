package main.scala

import java.io.File

import dispatch.github.{GhCommit, GhIssue}
import org.eclipse.jgit.api.Git

/**
  * Created by ErikL on 4/11/2017.
  */
class Repo(userName: String, repoName: String, token: String, labels: List[String], repoPath: String) {

  val debug = true
  val debugTreshhold = 10

  val git: Git = initGitRepo
  val repoInfo = Map("user" -> userName, "repo" -> repoName, "token" -> token)

  val commits: List[Commit] = getCommits
  val issues: List[Issue] = getIssues
  val faults: List[Fault] = getFaults


  private def initGitRepo: Git = {
    Git.cloneRepository().setURI("git@github.com:" + userName + "/" + repoName + ".git").setDirectory(new File(repoPath)).call
  }


  private def getCommits: List[Commit] = {
    def recursive(page: Int) : List[Commit] = {
      val commitsRes = GhCommit.get_commits(userName, repoName,  Map("page" -> page.toString, "per_page" -> "100", "access_token" -> token))()
      val commits = commitsRes.foldLeft(List[Commit]())((a, b) => a ::: List(new Commit(b, repoInfo)))
      if (commits.isEmpty || (debug && page > debugTreshhold))
        commits
      else
        commits ::: recursive(page + 1)
    }
    recursive(1)
  }


  private def getIssues: List[Issue] = {
    def recursive(page: Int, label: String) : List[Issue] = {
      val issuesRes = GhIssue.get_issues(userName, repoName,  Map("page" -> page.toString, "per_page" -> "100", "access_token" -> token, "state" -> "all", "labels" -> label))()
      val issues = issuesRes.foldLeft(List[Issue]())((a, b) => a ::: List(new Issue(b)))
      if (issues.isEmpty || (debug && page > debugTreshhold))
        issues
      else
        issues ::: recursive(page + 1, label)
    }

    labels.foldLeft(List[Issue]())((a,b) => a ::: recursive(1, b))
  }


  private def getFaults: List[Fault] = {
    commits.filter(isIssue).foldLeft(List[Fault]())((a, b) => a ::: List(new Fault(b, getIssues(b))))
  }


  private def isIssue(commit: Commit) : Boolean = {
    val pattern = """(?i)(clos(e[sd]?|ing)|fix(e[sd]|ing)?|resolv(e[sd]?)|#(\d+))""".r

    if ((pattern findAllIn commit.message).isEmpty)
      return false

    if (getIssues(commit).isEmpty)
      return false
    true
  }


  private def getIssues(commit: Commit) : List[Issue] = {
    val pattern = """#(\d+)""".r
    val possibleNumbers = pattern findAllIn commit.message

    if (possibleNumbers.isEmpty)
      return List[Issue]()

    val numbers = possibleNumbers.matchData.map(x => x.group(1).toInt).toList

    issues.filter(x => numbers contains x.number)
  }


  def checkoutCommit(commit: Commit): Unit = {
    git.checkout.setName(commit.sha).call
  }


  def checkoutPreviousCommit(commit: Commit): Unit = {
    git.checkout.setName(commit.commitData.parents.head.sha).call
  }
}
