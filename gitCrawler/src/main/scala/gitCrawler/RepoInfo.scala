package gitCrawler

import dispatch.github.{GhCommit, GhIssue}
import gitCrawler.util.GitDataBase
import java.text.SimpleDateFormat

import scala.annotation.tailrec


/**
  * Created by erikl on 4/26/2017.
  */
class RepoInfo(userName: String, repoName: String, token: String, labels: List[String], branch: String, repoPath: String) {
  private val debug = false
  private val debugTreshhold = 15
  private val info = Map("user" -> userName, "repo" -> repoName, "token" -> token, "repoPath" -> repoPath, "branch" -> branch)

  private val dataBase = new GitDataBase(info("repoPath"))
  private val iso8601DateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  val dbCommits: List[Commit] = dataBase.readList[Commit]("Commits").sortBy(x => x.date).reverse

  val commits: List[Commit] = removeDuplicates[Commit]((getCommits ::: dbCommits).distinct.sortBy(x => x.date).reverse, x => x.sha)
  val faults: List[Fault] = getFaults

  def removeDuplicates[T](list: List[T], id: (T) => Any): List[T] = {
    @tailrec
    def recursive(list: List[T], result: List[T]) : List[T] = list match {
      case Nil =>
        result
      case x :: tail =>
        if (tail.exists(y => id(y) == id(x)))
          recursive(tail, x :: result)
        else
          recursive(tail, x :: result)
    }

    recursive(list, List())
  }


  /**
    * Gets a list of all the commits from the repository
    *
    * @return
    */
  private def getCommits: List[Commit] = {
    def recursive(page: Int, since: String = null, until: String = null) : List[Commit] = {

      val commitsRes = if (since == null && until != null) {
        GhCommit.get_commits(userName, repoName, Map("page" -> page.toString, "access_token" -> token, "per_page" -> "100", "sha" -> info("branch"), "until" -> until))()
      } else if (since != null && until == null) {
        GhCommit.get_commits(userName, repoName, Map("page" -> page.toString, "access_token" -> token, "per_page" -> "100", "sha" -> info("branch"), "since" -> since))()
      } else {
        GhCommit.get_commits(userName, repoName, Map("page" -> page.toString, "access_token" -> token, "per_page" -> "100", "sha" -> info("branch")))()
      }

      val commits = commitsRes.foldLeft(List[Commit]())((a, b) => a ::: List(new Commit(b, info, null)))
      dataBase.writeList[Commit]("Commits", commits, x => x.sha)
      if (commits.isEmpty || (debug && page > debugTreshhold)){
        commits
      }else{
        commits ::: recursive(page + 1, since = since, until = until)
      }
    }

    if (dbCommits.nonEmpty) {
      val start = iso8601DateFormatter.format(dbCommits.head.date)
      val stop = iso8601DateFormatter.format(dbCommits.last.date)
      recursive(1, since = start) ::: recursive(1, until = stop)
    }
    else {
      recursive(1)
    }
  }


  /**
    * Gets a list of the fauls in the repository (combination of a commit and an issue)
    *
    * @return
    */
  private def getFaults: List[Fault] = {
    commits.filter(isIssue).foldLeft(List[Fault]())((a, b) => a ::: List(new Fault(b, List())))
  }


  /**
    * Checks if a commit fixes a issue or not
    *
    * @param commit the commit
    * @return
    */
  private def isIssue(commit: Commit) : Boolean = {
    val pattern = """(?i)(clos(e[sd]?|ing)|fix(e[sd]|ing)?|resolv(e[sd]?))""".r

    if ((pattern findAllIn commit.message).isEmpty)
      return false

    true
  }
}



