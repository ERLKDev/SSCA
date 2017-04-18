package main.scala.analyser.metric

import main.scala.analyser.result.MetricResult

/**
  * Created by Erik on 5-4-2017.
  */
trait FunctionMetric extends Metric{
  import global._
  def functionHeader: List[String]

  def run(tree: DefDef, code: List[String]): List[MetricResult]
}
