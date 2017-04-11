package main.scala.metrics

import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.{MetricResult, UnitType}
import main.scala.analyser.util.TreeUtil

/**
  * Created by ErikL on 4/7/2017.
  */
class DIT extends ObjectMetric with TreeUtil{
  import global._
  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Object , getName(tree), "DIT", getContext.getObjectByName(getName(tree)).DIT))
  }

  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Object , getName(tree), "DIT", getContext.getObjectByName(getName(tree)).DIT))
  }
}
