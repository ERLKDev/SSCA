package metrics

import analyser.metric.{FunctionMetric, ObjectMetric, ProjectMetric}
import analyser.result.{FunctionResult, MetricResult, ObjectResult}
import analyser.util.SourceCodeUtil



/**
  * Created by Erik on 5-4-2017.
  */
class Loc extends FunctionMetric with ObjectMetric with ProjectMetric with SourceCodeUtil{
  import global._

  override def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    val a = removeWhiteLines(code)
    List(new FunctionResult(getRangePos(tree), getName(tree), "loc", removeWhiteLines(code).size))
  }

  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    List(new ObjectResult(getRangePos(tree), getName(tree), "loc", removeWhiteLines(code).size))
  }

  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    val c = tree.mods.isAbstractOverride
    List(new ObjectResult(getRangePos(tree), getName(tree), "loc", removeWhiteLines(code).size))
  }

}
