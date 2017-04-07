package metrics

import analyser.metric.ObjectMetric
import analyser.result.{MetricResult, UnitType}
import analyser.util.TreeUtil

/**
  * Created by ErikL on 4/7/2017.
  */
class DIT extends ObjectMetric with TreeUtil{
  import global._
  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Object , getName(tree), "DIT", calculateDIT(getName(tree))))
  }

  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Object , getName(tree), "DIT", calculateDIT(getName(tree))))
  }

  private def calculateDIT(name: String): Int = {
    def recursive(parents: List[String]) : Int = parents match {
      case Nil =>
        0
      case x::tail =>
        val parent = projectContext.getObjectByName(x)
        if (parent != null && parent.isClass) {
          Math.max(1 + recursive(parent.getParents), recursive(tail))
        }else {
          Math.max(0, recursive(tail))
        }
    }

    1 + recursive(projectContext.getObjectByName(name).getParents)
  }
}
