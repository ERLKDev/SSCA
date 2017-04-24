package main.scala.analyser.metric

import analyser.AST._
import main.scala.analyser.result.MetricResult

/**
  * Created by Erik on 5-4-2017.
  */
trait FunctionMetric extends Metric{
  def functionHeader: List[String]

  def run(tree: FunctionDef, code: List[String]): List[MetricResult]
}
