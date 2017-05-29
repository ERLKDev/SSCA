package ssca

import codeAnalysis.STimer
import codeAnalysis.metrics._
import dispatch.github.GhIssueComment
import ssca.validator._

import scala.io.Source


/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  /**
    * Loads the Github token from the token file
    * @return
    */
  private def loadToken(): String = {
    val tokenFile = Source.fromFile("github.token")
    val githubToken = tokenFile.getLines.mkString
    tokenFile.close()
    githubToken
  }
  def main(args: Array[String]): Unit = {
    val repoUser = "scala"
    val repoName = "scala"

    val repoPath = "..\\tmp\\git" + repoUser.capitalize + repoName.capitalize

    val metrics = List(new Loc, new Complex, new DIT, new Inheritance, new OutDegree, new PatternSize, new WMC, new LCOM, new RFC, new NPVS, new PATC, new DON)
    val labels = List("bug")

    val validator = new ValidatorO(repoUser, repoName, repoPath, 3, 5, metrics, labels)

    //STimer.time("Analysis", validator.run(validator.writeHeaders, validator.objectOutput))
    STimer.time("Analysis", validator.run())

  }
}
