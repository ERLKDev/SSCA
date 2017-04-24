package main.scala.analyser.metric

import analyser.Compiler.TreeWrapper
import main.scala.analyser.result.MetricResult


/**
  * Created by Erik on 5-4-2017.
  */
trait ObjectMetric extends Metric{

  def objectHeader: List[String]

  def run(tree: TreeWrapper, code: List[String]): List[MetricResult]

  def run(tree: TreeWrapper, code: List[String]): List[MetricResult]
}
