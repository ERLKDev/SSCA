package main.scala.metrics

import analyser.AST._
import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by ErikL on 4/6/2017.
  */
class OutDegree extends FunctionMetric{

  override def functionHeader: List[String] = List("OutDegree", "OutDegreeDistinct")


  /**
    * Calculates the amount of method calls in a function
    *
    * @param tree the ast of the function
    * @param code the code of the function
    * @return
    */
  override def run(tree: FunctionDef, code: List[String]): List[MetricResult] = {
    def countFunctionCalls(tree : AST) : List[String] = tree match {
      case x : FunctionCall =>
        tree.children.foldLeft(List[String]())((a,b) => x.name :: a ::: countFunctionCalls(b))
      case _ =>
        tree.children.foldLeft(List[String]())((a,b) => a ::: countFunctionCalls(b))
    }

    val functionCalls = countFunctionCalls(tree)
    List(
      new MetricResult(tree.pos, tree.name + "$function", "OutDegree", functionCalls.size),
      new MetricResult(tree.pos, tree.name + "$function", "OutDegreeDistinct", functionCalls.distinct.size))
  }
}
