package main.scala.metrics

import main.scala.analyser.metric.FunctionMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by ErikL on 4/6/2017.
  */
class PatternSize extends FunctionMetric{
  import global._

  override def functionHeader: List[String] = List("PatternSize")


  /**
    * The pattern size of a function
    * This is calculated in ast nodes
    *
    * @param tree the ast of the function
    * @param code the code of the function
    * @return
    */
  override def run(tree: global.DefDef, code: List[String]): List[MetricResult] = {
    def count(tree: Tree) : Int = tree match {
      case _ =>
        tree.children.foldLeft(1)((a,b) => a + count(b))
    }
    List(new MetricResult(getRangePos(tree), getName(tree) + "$function", "PatternSize", count(tree)))
  }
}
