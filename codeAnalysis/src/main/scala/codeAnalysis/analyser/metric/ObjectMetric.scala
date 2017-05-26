package main.scala.analyser.metric

import main.scala.analyser.result.MetricResult
import codeAnalysis.analyser.AST._

/**
  * Created by Erik on 5-4-2017.
  */
trait ObjectMetric extends Metric{
  def objectHeader: List[String]

  /**
    * Function to execute the object metrics
    *
    * @param tree the ast tree of the object
    * @param code the code of the object
    * @return
    */
  def run(tree: ObjectDefinition, code: List[String]): List[MetricResult]

  /**
    * Function to execute the class metrics
    *
    * @param tree the ast tree of the class
    * @param code the code of the class
    * @return
    */
  def run(tree: ClassDefinition, code: List[String]): List[MetricResult]

  /**
    * Function to execute the trait metrics
    *
    * @param tree the ast tree of the trait
    * @param code the code of the trait
    * @return
    */
  def run(tree: TraitDefinition, code: List[String]): List[MetricResult]
}
