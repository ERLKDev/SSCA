package codeAnalysis.metrics

import codeAnalysis.analyser.AST._
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 5/11/2017.
  */
class RFC extends ObjectMetric {
  override def objectHeader: List[String] = List("RFC")

  override def run(tree: ObjectDefinition, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name, "RFC", getRFC(tree).distinct.length))
  }

  override def run(tree: ClassDefinition, code: List[String]): List[MetricResult] = {
    val a = getRFC(tree).distinct
    List(new MetricResult(tree.pos, tree.name, "RFC", getRFC(tree).distinct.length))
  }

  override def run(tree: TraitDefinition, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name, "RFC", getRFC(tree).distinct.length))
  }

  def getRFC(tree: AST): List[String] = tree match {
    case x: FunctionDef =>
      x.owner + x.name + "$FUNDEF" :: tree.children.foldLeft(List[String]())((a, b) => a ::: getRFC(b))
    case x: FunctionCall =>
      x.owner + x.name + "$FUNCALL" :: tree.children.foldLeft(List[String]())((a, b) => a ::: getRFC(b))
    case _ =>
      tree.children.foldLeft(List[String]())((a, b) => a ::: getRFC(b))
  }
}
