package gitCrawler

import java.io.File
import java.nio.file.Paths

import dispatch.github.{GhCommit, GhIssue}
import org.eclipse.jgit.api.{Git}
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.treewalk.CanonicalTreeParser

/**
  * Created by ErikL on 4/11/2017.
  */
class Repo(userName: String, repoName: String, repoPath: String, repoInfo: RepoInfo) {
  val git: Git = initGitRepo

  /**
    * Initializes the repository
    *
    * @return
    */
  private def initGitRepo: Git = {
    val file = new File(repoPath)
    if (!file.exists())
      GitR.runCommand(Paths.get(file.getParent), "git", "clone", "git@github.com:" + userName + "/" + repoName + ".git", repoPath)
    Git.open(file)
  }


  /**
    * Checkout to the commit
    *
    */
  def checkoutHead(): Unit = {
    GitR.runCommand(Paths.get(repoPath), "git", "reset", "--hard")
    GitR.runCommand(Paths.get(repoPath), "git", "checkout", "-f", "HEAD")
  }

  /**
    * Checkout to the commit
    *
    * @param commit the commit
    */
  def checkoutCommit(commit: Commit): Unit = {
    GitR.runCommand(Paths.get(repoPath), "git", "reset", "--hard")
    GitR.runCommand(Paths.get(repoPath), "git", "checkout", "-f", commit.sha)
  }


  /**
    * Checkout to the previous commit
    *
    * @param commit the commit
    */
  def checkoutPreviousCommit(commit: Commit): Unit = {
    GitR.runCommand(Paths.get(repoPath), "git", "reset", "--hard")
    GitR.runCommand(Paths.get(repoPath), "git", "checkout", "-f", getPreviousCommitSha(commit))
  }

  /**
    * Get The previous commit SHA
    *
    * @param commit the commit
    */
  def getPreviousCommitSha(commit: Commit): String = {
    commit.commitData.parents.head.sha
  }


  /**
    * Gets the file names that differ between two commits
    *
    * @param commit1 First commit
    * @param commit2 Second Commit
    * @return
    */
  def changedFiles(commit1: Commit, commit2: Commit) : List[String] = {
    try {
      val repository = git.getRepository

      val oldHead = repository.resolve(commit1.commitData.commit.tree.sha)
      val head = repository.resolve(repoInfo.commits.find(x => x.sha == getPreviousCommitSha(commit2)).get.commitData.commit.tree.sha)

      try {
        val reader = repository.newObjectReader()

        val oldTreeIter = new CanonicalTreeParser()
        oldTreeIter.reset(reader, oldHead)

        val newTreeIter = new CanonicalTreeParser()
        newTreeIter.reset(reader, head)

        try {
          val diffs = git.diff()
            .setNewTree(newTreeIter)
            .setOldTree(oldTreeIter)
            .call()
          return diffs.toArray[DiffEntry](Array[DiffEntry]()).filter(x => x.getChangeType != DiffEntry.ChangeType.DELETE)
            .foldLeft(List[String]())((a, b) => a ::: List(b.getNewPath)).filter(f => """.*\.scala$""".r.findFirstIn(f).isDefined)

        }catch {
          case _: Throwable =>
        }
      }
    }catch {
      case _: Throwable =>
    }
    List()
  }
}