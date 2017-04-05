package analyser.metric

import analyser.result.MetricResult


/**
  * Created by Erik on 5-4-2017.
  */
trait ObjectMetric extends Metric{
  import global._
  def run(tree: ModuleDef, code: List[String], projectTrees: Array[Tree], projectCode: List[String]): List[MetricResult]

  def run(tree: ClassDef, code: List[String], projectTrees: Array[Tree], projectCode: List[String]): List[MetricResult]
}
