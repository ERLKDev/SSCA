package main.scala.metrics

import main.scala.Utils.ComplexUtil
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.{MetricResult, UnitType}

/**
  * Created by ErikL on 4/6/2017.
  */
class WMC extends ObjectMetric with ComplexUtil{
  import global._

  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    wmc(tree, getName(tree))
  }

  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    wmc(tree, getName(tree))
  }

  def wmc(tree: Tree, name: String): List[MetricResult] = {
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
      MetricResult(getRangePos(tree), UnitType.Object, name,"WMCnormal", wmcNormal),
      MetricResult(getRangePos(tree), UnitType.Object, name,"WMCcc", wmcCC),
      MetricResult(getRangePos(tree), UnitType.Object, name,"WMCnestNormal", wmcNestNormal),
      MetricResult(getRangePos(tree), UnitType.Object, name,"WMCnestCC", wmcNestCC)
    )
  }
}
