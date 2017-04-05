package metrics

import analyser.metric.{FunctionMetric, ObjectMetric, ProjectMetric}
import analyser.result.{FunctionResult, MetricResult, ObjectResult}



/**
  * Created by Erik on 5-4-2017.
  */
class Loc extends FunctionMetric with ObjectMetric with ProjectMetric{
  import global._

  override def run(tree: DefDef, code: List[String], projectTrees: Array[Tree], projectCode: List[String]): List[MetricResult] = {
    List(new FunctionResult(getRangePos(tree), getName(tree), "loc", code.size))
  }

  override def run(tree: ModuleDef, code: List[String], projectTrees: Array[Tree], projectCode: List[String]): List[MetricResult] = {
    List(new ObjectResult(getRangePos(tree), getName(tree), "loc", code.size))
  }

  override def run(tree: ClassDef, code: List[String], projectTrees: Array[Tree], projectCode: List[String]): List[MetricResult] = {
    val c = tree.mods.isAbstractOverride
    List(new ObjectResult(getRangePos(tree), getName(tree), "loc", code.size))
  }
}
