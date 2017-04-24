package main.scala.metrics

import Utils.FunctionalUtil
import main.scala.Utils.SourceCodeUtil
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric}
import main.scala.analyser.result.MetricResult
import analyser.AST._


/**
  * Created by Erik on 5-4-2017.
  */
class Loc extends FunctionMetric with ObjectMetric with SourceCodeUtil with FunctionalUtil{

  override def functionHeader: List[String] = List("funLOC", "funSLOC", "funCD")
  override def objectHeader: List[String] = List("objLOC", "objSLOC", "objCD")


  /**
    * Measures the LOC, SLOC and CD of a function
    *
    * @param tree the ast of the function
    * @param code the code of the function
    * @return
    */
  override def run(tree: FunctionDef, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, tree.name, "function")
  }


  /**
    * Measures the LOC, SLOC and CD of a object
    *
    * @param tree the ast of the object
    * @param code the code of the object
    * @return
    */
  override def run(tree: ObjectDefinition, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, tree.name, "object")
  }


  /**
    * Measures the LOC, SLOC and CD of a class
    *
    * @param tree the ast of the class
    * @param code the code of the class
    * @return
    */
  override def run(tree: ClassDefinition, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, tree.name, "object")
  }


  /**
    * Measures the LOC, SLOC and CD of a trait
    *
    * @param tree the ast of the trait
    * @param code the code of the trait
    * @return
    */
  override def run(tree: TraitDefinition, code: List[String]): List[MetricResult] = {
    countLocs(code, tree, tree.name, "object")
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
  private def countLocs(code: List[String], tree: AST, name: String, prefix: String): List[MetricResult] = {
    val codeWithoutComments = removeComments(code)
    val codeWithComments = removeWhiteLines(code)
    val cd = if (codeWithComments.isEmpty) 0.0 else (codeWithComments.size - codeWithoutComments.size).asInstanceOf[Double] / codeWithComments.size
    List(
      new MetricResult(tree.pos, name+ "$" + prefix, prefix + "LOC", codeWithComments.size),
      new MetricResult(tree.pos, name+ "$" + prefix, prefix + "SLOC", codeWithoutComments.size),
      new MetricResult(tree.pos, name+ "$" + prefix, prefix + "CD", cd))
  }
}
