package codeAnalysis.metrics

import codeAnalysis.analyser.AST._
import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 5/15/2017.
  */
class NPVS extends FunctionMetric{
  override def functionHeader: List[String] = List("NPVS", "NPVSmatch", "NPVSmatchParms")

  override def run(tree: FunctionDef, code: List[String]): List[MetricResult] = {
    val b = matchParms(tree)

    List(new MetricResult(tree.pos, tree.name, "NPVS", countValDefs(tree).length),
      new MetricResult(tree.pos, tree.name, "NPVSmatch", matchOnly(tree).length),
      new MetricResult(tree.pos, tree.name, "NPVSmatchParms", matchParms(tree).length))
  }

  def matchOnly(tree: AST) : List[String] = tree match {
    case x: Case =>
      val defs = x.pattern.foldLeft(List[String]())((a, b) => a ::: countValDefs(b))
      tree.children.foldLeft(List[String]())((a, b) => a ::: matchOnly(b)) ::: defs
    case _ =>
      tree.children.foldLeft(List[String]())((a, b) => a ::: matchOnly(b))
  }

  def matchParms(tree: AST) : List[String] = tree match {
    case x: ValDefinition =>
      if (x.parameter)
        tree.children.foldLeft(List[String]())((a, b) => a ::: matchParms(b)) ::: List(x.owner + "." + x.name)
      else
        tree.children.foldLeft(List[String]())((a, b) => a ::: matchParms(b))
    case x: Case =>
      val defs = x.pattern.foldLeft(List[String]())((a, b) => a ::: countValDefs(b))
      tree.children.foldLeft(List[String]())((a, b) => a ::: matchParms(b)) ::: defs
    case _ =>
      tree.children.foldLeft(List[String]())((a, b) => a ::: matchParms(b))
  }

  def countValDefs(tree: AST) : List[String] = tree match {
    case x: ValueDefinition =>
      x.owner + "." + x.name :: tree.children.foldLeft(List[String]())((a, b) => a ::: countValDefs(b))
    case _ =>
      tree.children.foldLeft(List[String]())((a, b) => a ::: countValDefs(b))
  }
}
