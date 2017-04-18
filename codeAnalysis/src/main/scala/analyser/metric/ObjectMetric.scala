package main.scala.analyser.metric

import main.scala.analyser.result.MetricResult


/**
  * Created by Erik on 5-4-2017.
  */
trait ObjectMetric extends Metric{
  import global._
  def objectHeader: List[String]

  def run(tree: ModuleDef, code: List[String]): List[MetricResult]

  def run(tree: ClassDef, code: List[String]): List[MetricResult]
}
