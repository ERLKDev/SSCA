package main.scala.metrics

import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.{MetricResult, UnitType}
import main.scala.analyser.util.TreeUtil

/**
  * Created by ErikL on 4/7/2017.
  */
class DIT extends ObjectMetric with TreeUtil{
  import global._

  override def objectHeader: List[String] = List("DIT")

  /**
    * Count the depth of inheritance of an object
    *
    * @param tree the ast from the object
    * @param code the code from the object
    * @return
    */
  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Object , getName(tree), "DIT", countInheritanceDepth(tree.impl.parents)))
  }

  /**
    * Count the depth of inheritance of a class or trait
    *
    * @param tree the ast from the object
    * @param code the code from the object
    * @return
    */
  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Object , getName(tree), "DIT", countInheritanceDepth(tree.impl.parents)))
  }


  /**
    * Count the depth of inheritance from an object, class or trait
    *
    * @param parents List of parents of the original object, class or trait
    * @return
    */
  private def countInheritanceDepth(parents: List[Tree]) : Int = {
    def recursive(x: Symbol) : Int = {
      x.parentSymbols.foldLeft(0){ (a, b) =>
        if (b.isClass && !b.isTrait) {
          recursive(b) + 1
        }else
          a
      }
    }

    parents.foldLeft(1){
      (a, b) =>
        if (b.symbol.isClass && !b.symbol.isTrait) {
          1 + recursive(b.symbol)
        }else
          a
    }
  }
}
