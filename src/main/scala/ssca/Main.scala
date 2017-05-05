package ssca

import codeAnalysis.metrics.Inheritance
import main.scala.metrics._
import ssca.validator.{OValidator, Validator}


/**
  * Created by Erik on 13-4-2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    val repoUser = "akka"
    val repoName = "akka"
    val repoPath = "..\\tmp\\git" + repoUser.capitalize + repoName.capitalize

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT, new Inheritance)
    val labels = List("bug", "failed", "needs-attention")

    val validator = new Validator(repoUser, repoName, repoPath, 3, 5, metrics, labels)

    time(validator.run(validator.writeHeaders, validator.objectOutput))

  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Done in: " + (t1 - t0) + "ns (" + ((t1 - t0).toDouble / 1000000000.0) + "seconds)")
    result
  }
}
