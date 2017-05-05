package codeAnalysis.metrics

import codeAnalysis.analyser.AST._
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 5/5/2017.
  */
class Inheritance extends ObjectMetric{
  override def objectHeader: List[String] = List("Inheritance", "ClassInheritance", "TraitInheritance")

  override def run(tree: ObjectDefinition, code: List[String]): List[MetricResult] = {
    val (a, b, c) = countInheritanceDepth(tree.parents)
    List(
      new MetricResult(tree.pos, tree.name + "$object", "Inheritance", a),
      new MetricResult(tree.pos, tree.name + "$object", "ClassInheritance", b),
      new MetricResult(tree.pos, tree.name + "$object", "TraitInheritance", c)
    )
  }

  override def run(tree: ClassDefinition, code: List[String]): List[MetricResult] = {
    val (a, b, c) = countInheritanceDepth(tree.parents)
    List(
      new MetricResult(tree.pos, tree.name + "$object", "Inheritance", a),
      new MetricResult(tree.pos, tree.name + "$object", "ClassInheritance", b),
      new MetricResult(tree.pos, tree.name + "$object", "TraitInheritance", c)
    )
  }

  override def run(tree: TraitDefinition, code: List[String]): List[MetricResult] = {
    val (a, b, c) = countInheritanceDepth(tree.parents)
    List(
      new MetricResult(tree.pos, tree.name + "$object", "Inheritance", a),
      new MetricResult(tree.pos, tree.name + "$object", "ClassInheritance", b),
      new MetricResult(tree.pos, tree.name + "$object", "TraitInheritance", c)
    )
  }

  private def countInheritanceDepth(parents: List[Parent]) : (Int, Int, Int) = {
    parents.foldLeft((0, 0, 0)) {
      (a, b) =>
        b match {
          case x: ClassParent =>
            (a._1 + 1, a._2 + 1, a._3)
          case x: TraitParent =>
            (a._1 + 1, a._2, a._3 + 1)
        }
    }
  }
}
