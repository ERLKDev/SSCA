package metrics

import Utils.SourceCodeUtil
import analyser.metric.{FunctionMetric, ObjectMetric, ProjectMetric}
import analyser.result.{MetricResult, UnitType}


/**
  * Created by Erik on 5-4-2017.
  */
class Loc extends FunctionMetric with ObjectMetric with ProjectMetric with SourceCodeUtil{
  import global._

  override def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree))
  }

  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree))
  }

  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree))
  }

  //TODO fix comment remove function
  def countLocs(code: List[String], tree: Tree, name: String): List[MetricResult] = {
    val codeWithoutComments = removeComments(code)
    val codeWithComments = removeWhiteLines(code)
    val cd = (codeWithComments.size - codeWithoutComments.size).asInstanceOf[Double] / codeWithComments.size
    List(
      MetricResult(getRangePos(tree), UnitType.Function , name, "loc", codeWithComments.size),
      MetricResult(getRangePos(tree), UnitType.Function , name, "sloc", codeWithoutComments.size),
      MetricResult(getRangePos(tree), UnitType.Function , name, "cd", cd))
  }

}
