package metrics

import Utils.ComplexUtil
import analyser.metric.{FunctionMetric, ObjectMetric}
import analyser.result.{MetricResult, UnitType}

/**
  * Created by ErikL on 4/6/2017.
  */
class Complex extends FunctionMetric with ComplexUtil{

  import global._

  def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Function, getName(tree),"CC", measureComplexity(tree)))
  }
}
