package main.scala.analyser.metric

import codeAnalysis.analyser.AST._
import main.scala.analyser.result.MetricResult

/**
  * Created by Erik on 5-4-2017.
  */
trait FunctionMetric extends Metric{
  def functionHeader: List[String]

  /**
    * Function that should be called to run a function metric
    *
    * @param tree the ast of the function
    * @param code the code of the function
    * @return
    */
  def run(tree: FunctionDef, code: List[String]): List[MetricResult]
}
