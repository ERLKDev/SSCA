package main.scala.analyser.metric

import main.scala.analyser.result.MetricResult
import codeAnalysis.analyser.AST._

/**
  * Created by Erik on 5-4-2017.
  */
trait ObjectMetric extends Metric{

  def objectHeader: List[String]

  def run(tree: ObjectDefinition, code: List[String]): List[MetricResult]

  def run(tree: ClassDefinition, code: List[String]): List[MetricResult]

  def run(tree: TraitDefinition, code: List[String]): List[MetricResult]
}
