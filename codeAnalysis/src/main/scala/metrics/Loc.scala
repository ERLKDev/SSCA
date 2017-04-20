package main.scala.metrics

import Utils.FunctionalUtil
import main.scala.Utils.SourceCodeUtil
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric, ProjectMetric}
import main.scala.analyser.result.MetricResult
import main.scala.analyser.util.TreeSyntaxUtil


/**
  * Created by Erik on 5-4-2017.
  */
class Loc extends FunctionMetric with ObjectMetric with ProjectMetric with SourceCodeUtil with TreeSyntaxUtil with FunctionalUtil{
  import global._

  override def functionHeader: List[String] = List("funLOC", "funSLOC", "funCD")
  override def objectHeader: List[String] = List("objLOC", "objSLOC", "objCD")


  /**
    * Measures the LOC, SLOC and CD of a function
    *
    * @param tree the ast of the function
    * @param code the code of the function
    * @return
    */
  override def run(tree: DefDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree), "fun")
  }


  /**
    * Measures the LOC, SLOC and CD of a object
    *
    * @param tree the ast of the object
    * @param code the code of the object
    * @return
    */
  override def run(tree: ModuleDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree), "obj")
  }


  /**
    * Measures the LOC, SLOC and CD of a class or trait
    *
    * @param tree the ast of the class or trait
    * @param code the code of the class or trait
    * @return
    */
  override def run(tree: ClassDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, getName(tree), "obj")
  }


  /**
    * Fuynction to measure the LOC, SLOC and CD of an unit of code
    *
    * @param code an unit of code
    * @param tree the ast tree of the code
    * @param name the name of the function, class, trait or object
    * @param prefix the prefix for the metric name
    * @return
    */
  private def countLocs(code: List[String], tree: Tree, name: String, prefix: String): List[MetricResult] = {
    val codeWithoutComments = removeComments(code)
    val codeWithComments = removeWhiteLines(code)
    val cd = if (codeWithComments.isEmpty) 0.0 else (codeWithComments.size - codeWithoutComments.size).asInstanceOf[Double] / codeWithComments.size
    List(
      new MetricResult(getRangePos(tree), name,  prefix + "LOC", codeWithComments.size),
      new MetricResult(getRangePos(tree), name,  prefix + "SLOC", codeWithoutComments.size),
      new MetricResult(getRangePos(tree), name, prefix + "CD", cd))
  }
}
