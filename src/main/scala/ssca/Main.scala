package ssca

import codeAnalysis.STimer
import codeAnalysis.metrics.{PatternSize, _}
import dispatch.Http
import ssca.validator._


/**
  * Created by Erik on 13-4-2017.
  */
object Main {

  def main(args: Array[String]): Unit = {
    val repoUser = "gitbucket"
    val repoName = "gitbucket"
    val repoPath = "..\\tmp"
    val project = "GIT"
    val version = "Final"
    val branch = "master"
    val labels = List("bug")

    val metrics = List(new DIT, new LCOM, new RFC, new WMC, new NOC, new CBO, new PatternSize, new OutDegree, new NPVS, new DON, new Loc, new Complex, new FunctionalMetrics, new Inheritance)

    val validatorN = new ValidatorNObject(repoPath, repoUser, repoName, branch, labels, 3, 5, metrics, "fullOutput" + project + version + "New")
    STimer.time("Analysis", validatorN.run())

    val validatorO = new ValidatorOObject(repoPath, repoUser, repoName, branch, labels, 3, 5, metrics, "fullOutput" + project + version + "Old")
    STimer.time("Analysis", validatorO.run())
    Http.shutdown()
  }
}
