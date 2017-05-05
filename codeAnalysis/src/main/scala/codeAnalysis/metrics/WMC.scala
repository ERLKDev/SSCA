package codeAnalysis.metrics

import main.scala.Utils.ComplexUtil
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult
import codeAnalysis.analyser.AST._

/**
  * Created by ErikL on 4/6/2017.
  */
class WMC extends ObjectMetric with ComplexUtil{

  override def objectHeader: List[String] = List("WMCnormal", "WMCcc", "WMCnormalInit", "WMCccInit")

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
    var wmcNormalInit = 0
    var wmcCCInit = 0

    def recursive(tree: AST): Unit = tree match {
      case x: FunctionDef => {
        if (x.name != "<init>") {
          wmcCC += measureComplexity(x, false)
          wmcNormal += 1
        }
        tree.children.foreach(recursive)
      }
      case (_: ClassDefinition | _: ObjectDefinition ) =>

      case _ =>
        tree.children.foreach(recursive)
    }
    tree.children.foreach(recursive)

    wmcCCInit = wmcNormal + measureComplexity(tree)
    wmcNormalInit = 1 + wmcNormal

    List(
      new MetricResult(tree.pos, name + "$object", "WMCnormal", wmcNormal),
      new MetricResult(tree.pos, name + "$object", "WMCcc", wmcCC),
      new MetricResult(tree.pos, name + "$object", "WMCnormalInit", wmcNormalInit),
      new MetricResult(tree.pos, name + "$object", "WMCccInit", wmcCCInit)
    )
  }
}
