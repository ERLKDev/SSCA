package analyser.metric

import analyser.result.MetricResult

/**
  * Created by Erik on 5-4-2017.
  */
trait FunctionMetric extends Metric{
  import global._
  def run(tree: DefDef, code: List[String]): List[MetricResult]
}
