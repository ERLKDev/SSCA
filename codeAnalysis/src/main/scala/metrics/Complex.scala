package main.scala.metrics

import main.scala.Utils.ComplexUtil
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric}
import main.scala.analyser.result.MetricResult

/**
  * Created by ErikL on 4/6/2017.
  */
class Complex extends FunctionMetric with ComplexUtil{
  import global._

  override def functionHeader: List[String] = List("CC")

  /**
    * Measures the cyclomatic Complexity (CC) of a function
    * @param tree the ast of a function
    * @param code the code of a function
    * @return
    */
  def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    List(new MetricResult(getRangePos(tree), getName(tree) + "$function","CC", measureComplexity(tree)))
  }
}
