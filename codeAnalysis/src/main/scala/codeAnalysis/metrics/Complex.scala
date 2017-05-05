package main.scala.metrics

import main.scala.Utils.ComplexUtil
import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult
import codeAnalysis.analyser.AST._

/**
  * Created by ErikL on 4/6/2017.
  */
class Complex extends FunctionMetric with ComplexUtil{

  override def functionHeader: List[String] = List("CC")

  /**
    * Measures the cyclomatic Complexity (CC) of a function
    * @param tree the ast of a function
    * @param code the code of a function
    * @return
    */
  def run(tree: FunctionDef, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name + "$function","CC", measureComplexity(tree, false)))
  }
}
