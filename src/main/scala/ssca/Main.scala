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
    val repoUser = "gitbucket"
    val repoName = "gitbucket"
    val repoPath = "..\\tmp"

    dataSets.foreach{
      x =>
        val labels = List("bug")
        val validator = new ValidatorNObject(repoPath, repoUser, repoName, "master", labels, 3, 5, x._2, "fullOutputGIT004" + x._1)

        //STimer.time("Analysis", validator.run(validator.writeHeaders, validator.objectOutput))
        STimer.time("Analysis", validator.run())
    }
    Http.shutdown()
  }
}
