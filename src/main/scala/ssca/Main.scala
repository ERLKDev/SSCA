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

    val metrics = List(new Loc, new Complex, new DIT, new Inheritance, new OutDegree, new PatternSize, new WMC)
    val labels = List("bug")

    val validator = new OValidator(repoUser, repoName, repoPath, 5, metrics, labels)

    STimer.time("Analysis", validator.run(validator.writeHeaders))
    //STimer.time("Analysis", validator.run(validator.writeHeaders, validator.objectOutput))

  }
}
