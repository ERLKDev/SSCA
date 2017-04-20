package main.scala.metrics

import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by ErikL on 4/6/2017.
  */
class OutDegree extends FunctionMetric{
  import global._

  override def functionHeader: List[String] = List("OutDegree", "OutDegreeDistinct")


  /**
    * Calculates the amount of method calls in a function
    *
    * @param tree the ast of the function
    * @param code the code of the function
    * @return
    */
  override def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    def countFunctionCalls(tree : Tree) : List[String] = tree match {
      case _ : Select =>
        tree.symbol.name.toString
        tree.children.foldLeft(List[String]())((a,b) => (if (tree.symbol.isMethod) List(tree.symbol.name.toString) else List[String]()) ::: a ::: countFunctionCalls(b))
      case _ =>
        tree.children.foldLeft(List[String]())((a,b) => a ::: countFunctionCalls(b))
    }

    val functionCalls = countFunctionCalls(tree)
    List(
      new MetricResult(getRangePos(tree), getName(tree) + "$function", "OutDegree", functionCalls.size),
      new MetricResult(getRangePos(tree), getName(tree) + "$function", "OutDegreeDistinct", functionCalls.distinct.size))
  }
}
