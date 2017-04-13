package main.scala.metrics

import main.scala.Utils.SourceCodeUtil
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric, ProjectMetric}
import main.scala.analyser.result.UnitType.UnitType
import main.scala.analyser.result.{MetricResult, UnitType}


/**
  * Created by Erik on 5-4-2017.
  */
class Loc extends FunctionMetric with ObjectMetric with ProjectMetric with SourceCodeUtil{
  import global._

  override def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree), UnitType.Function)
  }

  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree), UnitType.Object)
  }

  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree), UnitType.Object)
  }

  //TODO fix comment remove function
  def countLocs(code: List[String], tree: Tree, name: String, uType: UnitType): List[MetricResult] = {
    val codeWithoutComments = removeComments(code)
    val codeWithComments = removeWhiteLines(code)
    val cd = (codeWithComments.size - codeWithoutComments.size).asInstanceOf[Double] / codeWithComments.size
    List(
      MetricResult(getRangePos(tree), uType, name, "loc", codeWithComments.size),
      MetricResult(getRangePos(tree), uType, name, "sloc", codeWithoutComments.size),
      MetricResult(getRangePos(tree), uType, name, "cd", cd))
  }

}
