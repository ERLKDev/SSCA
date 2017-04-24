package main.scala.metrics

import analyser.Compiler.{CompilerS, TreeWrapper}
import main.scala.Utils.ComplexUtil
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric}
import main.scala.analyser.result.MetricResult
import main.scala.analyser.util.TreeUtil

/**
  * Created by ErikL on 4/6/2017.
  */
class Complex extends FunctionMetric with ComplexUtil with TreeUtil{

  override def functionHeader: List[String] = List("CC")

  /**
    * Measures the cyclomatic Complexity (CC) of a function
    * @param wrappedTree the ast of a function
    * @param code the code of a function
    * @return
    */
  def run(wrappedTree: TreeWrapper, code: List[String]): List[MetricResult] = {
    val tree = wrappedTree.unWrap()
    List(new MetricResult(getRangePos(wrappedTree), getName(wrappedTree) + "$function","CC", measureComplexity(wrappedTree)))
  }
}
