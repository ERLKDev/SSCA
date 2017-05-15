package codeAnalysis.metrics

import codeAnalysis.analyser.AST.{AST, FunctionDef}
import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 5/15/2017.
  */
class DON extends FunctionMetric{
  override def functionHeader: List[String] = List("DON")

  override def run(tree: FunctionDef, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name, "DON", countNesting(tree)))
  }

  def countNesting(tree: AST) : Int = {
    tree.children.foldLeft(0)((a, b) => math.max(a, countNesting(b))) + 1
  }
}
