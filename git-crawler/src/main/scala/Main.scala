import java.io.File

import main.scala.Repo
import org.eclipse.jgit.api.{CreateBranchCommand, Git}
import org.eclipse.jgit.lib.Constants
/**
  * Created by ErikL on 4/11/2017.
  */
object Main {
  implicit val formats = net.liftweb.json.DefaultFormats
  def main(args: Array[String]): Unit = {

    val repo = new Repo("akka", "akka", "aa5065d38b6ea9e9865b176920b315ba9e63250f", List("bug", "failed", "needs-attention "), "test")
/*
    val repo = new Repo("akka", "akka", "aa5065d38b6ea9e9865b176920b315ba9e63250f", List("bug", "failed", "needs-attention "))
    val faults = repo.faults

    faults.foreach{
      x =>
        println(x.commit.commitData.sha)
    }

*/
    val f = new File("test")
    val git = Git.open(f)
/*    val f = new File("test")
    val git = Git.cloneRepository()
      .setURI("git@github.com:akka/akka.git")
      .setDirectory(f)
      .call*/

/*    git.fetch.setRemote("git@github.com:akka/akka.git").set.call*/
/*    git.checkout().
      setCreateBranch(true).
      setName("master").
      setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
      call()*/
    println("checkout 1")
    repo.faults(0).commit
    repo.checkoutCommit(repo.faults(0).commit)

    println("checkout 2")
    repo.faults(1).commit
    repo.checkoutCommit(repo.faults(1).commit)

    println("checkout 3")
    repo.faults(2).commit
    repo.checkoutCommit(repo.faults(2).commit)

    println("checkout 1 prev")
    repo.faults(0).commit
    repo.checkoutPreviousCommit(repo.faults(0).commit)

    println("checkout 2 prev")
    repo.faults(1).commit
    repo.checkoutPreviousCommit(repo.faults(1).commit)

    println("checkout 3 prev")
    repo.faults(2).commit
    repo.checkoutPreviousCommit(repo.faults(2).commit)



    println("done getting commits")
  }
}
