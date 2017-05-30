package gitCrawler

import dispatch.github.{GhCommit, GhIssue, GhIssueComment, GhPullCommit}

/**
  * Created by erikl on 4/26/2017.
  */
class RepoInfo(userName: String, repoName: String, token: String, labels: List[String], branch: String, repoPath: String) {
  private val debug = true
  private val debugTreshhold = 1
  private val info = Map("user" -> userName, "repo" -> repoName, "token" -> token, "repoPath" -> repoPath, "branch" -> branch)

  lazy val commits: List[Commit] = getCommits
  val issues: List[Issue] = getIssues
  val faults: List[Fault] = getFaults


  /**
    * Gets a list of all the commits from the repository
    *
    * @return
    */
  private def getCommits: List[Commit] = {
    def recursive(page: Int) : List[Commit] = {
      val commitsRes = GhCommit.get_commits(userName, repoName,  Map("page" -> page.toString, "access_token" -> token, "per_page" -> "100", "sha" -> info("branch")))()
      val commits = commitsRes.foldLeft(List[Commit]())((a, b) => a ::: List(new Commit(b, info, null)))
      if (commits.isEmpty || (debug && page > debugTreshhold))
        commits
      else
        commits ::: recursive(page + 1)
    }
    recursive(1)
  }


  /**
    * Gets a list of all the issues from the repository
    *
    * @return
    */
  private def getIssues: List[Issue] = {
    def recursive(page: Int, label: String) : List[Issue] = {
      val issuesRes = GhIssue.get_issues("scala", "bug",  Map("page" -> page.toString, "per_page" -> "100", "access_token" -> token, "state" -> "all"))()
      val issues = issuesRes.foldLeft(List[Issue]())((a, b) => a ::: List(new Issue(b)))
      if (issues.isEmpty || (debug && page > debugTreshhold))
        issues
      else
        issues ::: recursive(page + 1, label)
    }

    labels.foldLeft(List[Issue]())((a,b) => a ::: recursive(1, b))
  }


  /**
    * Gets a list of the fauls in the repository (combination of a commit and an issue)
    *
    * @return
    */
  private def getFaults: List[Fault] = {
    issues.filter(isIssue).foldLeft(List[Fault]()){
      (a, b) =>
        val commits = getIssueCommits(b)
        a ::: commits.foldLeft(List[Fault]())((x, y) => x ::: List(new Fault(y, List(b))))
    }
  }



  private def isIssue(issue: Issue) : Boolean = {
    val comments = GhIssueComment.get_comments("scala", "bug", issue.number, Map("access_token" -> token))()
    comments.exists{
      x =>
        ("""github\.com\/scala\/scala\/pull\/(\d*)""".r findAllIn x.body).nonEmpty
    }
  }


  /**
    * Gets a list of issues that are mentioned as fixed in the commit
    *
    * @param issue the commit
    * @return
    */
  private def getIssueCommits(issue: Issue) : List[Commit] = {
    def recursive(page: Int, number: Int) : List[Commit] = {
      val commitsRes =  GhPullCommit.get_pull_commits(userName, repoName, number,  Map("page" -> page.toString, "access_token" -> token, "per_page" -> "100"))()
      val commitsF = commitsRes.filter(x => commits.exists(y => y.sha == x.sha)).foldLeft(List[Commit]())((a, b) => a ::: List(new Commit(b, info, null)))
      if (commitsF.isEmpty || (debug && page > debugTreshhold))
        commitsF
      else
        commitsF ::: recursive(page + 1, number)
    }


    val comments = GhIssueComment.get_comments("scala", "bug", issue.number, Map("access_token" -> token))()
    val numbers = comments.foldLeft(List[Int]()){
      (a, b) =>
        val matches ="""github\.com\/scala\/scala\/pull\/(\d*)""".r findAllIn b.body
        if (matches.isEmpty)
          a
        else
          a ::: matches.matchData.map(x => x.group(1).toInt).toList
    }

    numbers.foldLeft(List[Commit]()){
      (a, b) =>
        a ::: recursive(1, b)
    }
  }
}



