package ssca

import codeAnalysis.STimer
import codeAnalysis.metrics.{PatternSize, _}
import dispatch.Http
import main.scala.analyser.metric.Metric
import ssca.validator._


/**
  * Created by Erik on 13-4-2017.
  */
object Main {

  val dataSets : List[(String, List[Metric])] = List(
    ("Obj", List(new DIT, new LCOM, new RFC, new WMC, new NOC, new CBO)),
    ("ObjG", List(new DIT, new LCOM, new RFC, new WMC, new NOC, new CBO, new Loc, new Complex)),
    ("Func", List(new PatternSize, new OutDegree, new NPVS, new DON)),
    ("FuncG", List(new PatternSize, new OutDegree, new NPVS, new DON, new Loc, new Complex)),
    ("FuncS", List(new PatternSize, new OutDegree, new NPVS, new DON, new FunctionalMetrics)),
    ("FuncGS", List(new PatternSize, new OutDegree, new NPVS, new DON, new FunctionalMetrics, new Loc, new Complex)),
    ("Gen", List(new Loc, new Complex)),
    ("GenS", List(new Loc, new Complex, new FunctionalMetrics)),
    ("Comb", List(new DIT, new LCOM, new RFC, new WMC, new NOC, new CBO, new PatternSize, new OutDegree, new NPVS, new DON)),
    ("CombG", List(new DIT, new LCOM, new RFC, new WMC, new NOC, new CBO, new PatternSize, new OutDegree, new NPVS, new DON, new Loc, new Complex)),
    ("CombS", List(new DIT, new LCOM, new RFC, new WMC, new NOC, new CBO, new PatternSize, new OutDegree, new NPVS, new DON, new FunctionalMetrics)),
    ("CombGS", List(new DIT, new LCOM, new RFC, new WMC, new NOC, new CBO, new PatternSize, new OutDegree, new NPVS, new DON, new Loc, new Complex, new FunctionalMetrics))
  )


  def main(args: Array[String]): Unit = {
    val repoUser = "shadowsocks"
    val repoName = "shadowsocks-android"
    val repoPath = "..\\tmp"
    val project = "SHA"
    val version = "005"
    val branch = "master"
    val labels = List("bug")

    dataSets.foreach{
      x =>
        val validator = new ValidatorNObject(repoPath, repoUser, repoName, branch, labels, 3, 5, x._2, "fullOutput" + project + version + "New" + x._1)
        STimer.time("Analysis", validator.run())
    }
    dataSets.foreach{
      x =>
        val validator = new ValidatorOObject(repoPath, repoUser, repoName, branch, labels, 3, 5, x._2, "fullOutput" + project + version + "Old" + x._1)
        STimer.time("Analysis", validator.run())
    }
    Http.shutdown()
  }
}
