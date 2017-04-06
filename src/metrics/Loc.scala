package metrics

import analyser.metric.{FunctionMetric, ObjectMetric, ProjectMetric}
import analyser.result.{MetricResult, UnitType}
import analyser.util.SourceCodeUtil



/**
  * Created by Erik on 5-4-2017.
  */
class Loc extends FunctionMetric with ObjectMetric with ProjectMetric with SourceCodeUtil{
  import global._

  override def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    List(new MetricResult(getRangePos(tree), UnitType.Function ,getName(tree), "loc", removeWhiteLines(code).size))
  }

  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    List(new MetricResult(getRangePos(tree), UnitType.Object, getName(tree), "loc", removeWhiteLines(code).size))
  }

  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    List(new MetricResult(getRangePos(tree), UnitType.Object, getName(tree), "loc", removeWhiteLines(code).size))
  }

}
