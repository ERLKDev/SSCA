package ssca

import codeAnalysis.STimer
import codeAnalysis.metrics._
import ssca.validator.{OValidator, Validator}


/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val repoUser = "akka"
    val repoName = "akka"
    val repoPath = "..\\tmp\\git" + repoUser.capitalize + repoName.capitalize

    val metrics = List(new Loc)
    val labels = List("bug", "failed", "needs-attention")

    val validator = new Validator(repoUser, repoName, repoPath, 3, 5, metrics, labels)

    STimer.time("Analysis", validator.run(validator.writeHeaders, validator.objectOutput))

  }
}
