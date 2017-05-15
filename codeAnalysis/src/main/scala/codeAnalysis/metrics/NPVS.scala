package codeAnalysis.metrics

import codeAnalysis.analyser.AST.FunctionDef
import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 5/15/2017.
  */
class NPVS extends FunctionMetric{
  override def functionHeader: List[String] = List("NPVS","NPVSmatch")

  override def run(tree: FunctionDef, code: List[String]): List[MetricResult] = {
    List()
  }
}
