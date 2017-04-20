package main.scala.metrics

import main.scala.Utils.ComplexUtil
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by ErikL on 4/6/2017.
  */
class WMC extends ObjectMetric with ComplexUtil{
  import global._

  override def objectHeader: List[String] = List("WMCnormal", "WMCcc", "WMCnestNormal", "WMCnestCC")

  /**
    * Calculates the weighted method complexity
    * This is done by adding the CC of each function in the object
    *
    * @param tree the ast of the object
    * @param code the code of the object
    * @return
    */
  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    wmc(tree, getName(tree))
  }


  /**
    * Calculates the weighted method complexity
    * This is done by adding the CC of each function in the class or trait
    *
    * @param tree the ast of the object
    * @param code the code of the object
    * @return
    */
  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    wmc(tree, getName(tree))
  }


  /**
    * Function to calculate the WMC
    *
    * @param tree the ast of the object, class or trait
    * @param name the code of the object, class or trait
    * @return
    */
  private def wmc(tree: Tree, name: String): List[MetricResult] = {
    var wmcNormal = 0
    var wmcCC = 0
    var wmcNestNormal = 0
    var wmcNestCC = 0

    def allFunctions(tree: Tree, level: Int): Unit = tree match {
      case x: DefDef => {
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
      new MetricResult(getRangePos(tree), name + "$object", "WMCnormal", wmcNormal),
      new MetricResult(getRangePos(tree), name + "$object", "WMCcc", wmcCC),
      new MetricResult(getRangePos(tree), name + "$object", "WMCnestNormal", wmcNestNormal),
      new MetricResult(getRangePos(tree), name + "$object", "WMCnestCC", wmcNestCC)
    )
  }
}
