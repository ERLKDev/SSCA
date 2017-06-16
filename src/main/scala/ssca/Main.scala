package ssca

import codeAnalysis.STimer
import codeAnalysis.metrics._
import ssca.validator._


/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val repoUser = "akka"
    val repoName = "akka"
    val repoPath = "..\\tmp"

    val metrics = List(new Loc, new Complex, new DIT, new Inheritance, new OutDegree, new PatternSize, new WMC, new LCOM, new RFC, new NPVS, new PATC, new DON)
    val labels = List("bug")

    val validator = new ValidatorNObject(repoPath, repoUser, repoName, "release-2.4-http", labels, 3, 5, metrics)

    //STimer.time("Analysis", validator.run(validator.writeHeaders, validator.objectOutput))
    STimer.time("Analysis", validator.run())

  }
}
