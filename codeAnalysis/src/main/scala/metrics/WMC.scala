package main.scala.metrics

import main.scala.Utils.ComplexUtil
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult
import analyser.AST._

/**
  * Created by ErikL on 4/6/2017.
  */
class WMC extends ObjectMetric with ComplexUtil{

  override def objectHeader: List[String] = List("WMCnormal", "WMCcc", "WMCnestNormal", "WMCnestCC")

  /**
    * Calculates the weighted method complexity
    * This is done by adding the CC of each function in the object
    *
    * @param tree the ast of the object
    * @param code the code of the object
    * @return
    */
  override def run(tree: ObjectDefinition, code: List[String]): List[MetricResult] = {
    wmc(tree, tree.name)
  }


  /**
    * Calculates the weighted method complexity
    * This is done by adding the CC of each function in the class
    *
    * @param tree the ast of the object
    * @param code the code of the object
    * @return
    */
  override def run(tree: ClassDefinition, code: List[String]): List[MetricResult] = {
    wmc(tree, tree.name)
  }


  /**
    * Calculates the weighted method complexity
    * This is done by adding the CC of each function in the trait
    *
    * @param tree the ast of the object
    * @param code the code of the object
    * @return
    */
  override def run(tree: TraitDefinition, code: List[String]): List[MetricResult] = {
    wmc(tree, tree.name)
  }


  /**
    * Function to calculate the WMC
    *
    * @param tree the ast of the object, class or trait
    * @return
    */
  private def wmc(tree: AST, name: String): List[MetricResult] = {
    var wmcNormal = 0
    var wmcCC = 0
    var wmcNestNormal = 0
    var wmcNestCC = 0

    def allFunctions(tree: AST, level: Int): Unit = tree match {
      case x: FunctionDef => {
        wmcNestNormal += 1
        wmcNestCC += measureComplexity(x)
        if (level == 0) {
          wmcCC += measureComplexity(x)
          wmcNormal += 1
        }
        tree.children.foreach(x => allFunctions(x, level + 1))
      }
      case _ =>
        tree.children.foreach(x => allFunctions(x, level))
    }

    allFunctions(tree, 0)
    List(
      new MetricResult(tree.pos, name + "$object", "WMCnormal", wmcNormal),
      new MetricResult(tree.pos, name + "$object", "WMCcc", wmcCC),
      new MetricResult(tree.pos, name + "$object", "WMCnestNormal", wmcNestNormal),
      new MetricResult(tree.pos, name + "$object", "WMCnestCC", wmcNestCC)
    )
  }
}
