package main.scala.metrics

import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult
import analyser.AST._

/**
  * Created by ErikL on 4/7/2017.
  */
class DIT extends ObjectMetric {

  override def objectHeader: List[String] = List("DIT")

  /**
    * Count the depth of inheritance of an object
    *
    * @param tree the ast from the object
    * @param code the code from the object
    * @return
    */
  override def run(tree: ObjectDefinition, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name + "$object", "DIT", countInheritanceDepth(tree.parents)))
  }

  /**
    * Count the depth of inheritance of a class or trait
    *
    * @param tree the ast from the object
    * @param code the code from the object
    * @return
    */
  override def run(tree: ClassDefinition, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name + "$object", "DIT", countInheritanceDepth(tree.parents)))
  }

  override def run(tree: TraitDefinition, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name + "$object", "DIT", countInheritanceDepth(tree.parents)))
  }


  /**
    * Count the depth of inheritance from an object, class or trait
    *
    * @param parents List of parents of the original object, class or trait
    * @return
    */
  private def countInheritanceDepth(parents: List[Parent]) : Int = {
    parents.foldLeft(0){
      (a, b) =>
        b match {
          case x: ClassParent =>
            1 + countInheritanceDepth(x.parents)
          case _ =>
            a
        }
    }
  }
}
