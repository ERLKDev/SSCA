package codeAnalysis.metrics

import codeAnalysis.Utils.FunctionalUtil
import codeAnalysis.analyser.AST.FunctionDef
import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 6/1/2017.
  */
class FunctionalMetrics extends FunctionMetric with FunctionalUtil{
  override def functionHeader: List[String] = List("SideEffects", "Recursive", "Nested", "FunctionalCalls", "HigherOrder")

  /**
    * Function that should be called to run a function metric
    *
    * @param tree the ast of the function
    * @param code the code of the function
    * @return
    */
  override def run(tree: FunctionDef, code: List[String]): List[MetricResult] = {
    val recursive = if (isRecursive(tree)) 1.0 else 0.0
    val nested = if (isNested(tree)) 1.0 else 0.0

    List(
      new MetricResult(tree.pos, tree.name, "SideEffects", countSideEffects(tree)),
      new MetricResult(tree.pos, tree.name, "Recursive", recursive),
      new MetricResult(tree.pos, tree.name, "Nested", nested),
      new MetricResult(tree.pos, tree.name, "FunctionalCalls", countFunctionalFuncCalls(tree)),
      new MetricResult(tree.pos, tree.name, "HigherOrder", countHigherOrderParams(tree))
    )
  }
}
