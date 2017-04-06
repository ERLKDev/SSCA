package metrics

import analyser.metric.FunctionMetric
import analyser.result.{MetricResult, UnitType}

/**
  * Created by ErikL on 4/6/2017.
  */
class OutDegree extends FunctionMetric{
  import global._
  override def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    def countFunctionCalls(tree : Tree) : List[String] = tree match {
      case _ : Select =>
        tree.symbol.name.toString
        tree.children.foldLeft(List[String]())((a,b) => (if (tree.symbol.isMethod) List(tree.symbol.name.toString) else List[String]()) ::: a ::: countFunctionCalls(b))
      case _ =>
        tree.children.foldLeft(List[String]())((a,b) => a ::: countFunctionCalls(b))
    }
    println(countFunctionCalls(tree))

    val functionCalls = countFunctionCalls(tree)
    List(
      MetricResult(getRangePos(tree), UnitType.Function, getName(tree), "OutDegree", functionCalls.size),
      MetricResult(getRangePos(tree), UnitType.Function, getName(tree), "OutDegreeDistinct", functionCalls.distinct.size))
  }
}
