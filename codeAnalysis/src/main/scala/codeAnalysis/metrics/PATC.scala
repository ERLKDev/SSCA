package codeAnalysis.metrics

import codeAnalysis.analyser.AST.{AST, Case, FunctionDef}
import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 5/15/2017.
  */
class PATC extends FunctionMetric{
  override def functionHeader: List[String] = List("PATCmatch")

  override def run(tree: FunctionDef, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name, "PATCmatch", countMatchConstructors(tree)))
  }

  def countMatchConstructors(tree: AST) : Int = tree match {
    case x: Case =>
      tree.children.foldLeft(1)((a, b) => a + countMatchConstructors(b))
    case _ =>
      tree.children.foldLeft(0)((a, b) => a + countMatchConstructors(b))
  }
}
