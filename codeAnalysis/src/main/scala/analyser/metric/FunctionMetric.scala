package main.scala.analyser.metric

import analyser.Compiler.{CompilerS, TreeWrapper}
import main.scala.analyser.result.MetricResult

/**
  * Created by Erik on 5-4-2017.
  */
trait FunctionMetric extends Metric{

  def functionHeader: List[String]

  def run(tree: TreeWrapper, code: List[String]): List[MetricResult]

}
