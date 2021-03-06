package codeAnalysis.metrics

import codeAnalysis.analyser.AST._
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult

import scala.annotation.tailrec

/**
  * Created by erikl on 5/10/2017.
  */
class LCOM extends ObjectMetric {
  override def objectHeader: List[String] = List("LCOM", "LCOMneg")

  override def run(tree: ObjectDefinition, code: List[String]): List[MetricResult] = {
    val (lcom, lcomN) = getLCOM(tree, false)
    List(new MetricResult(tree.pos, tree.name + "$object", "LCOM", lcom),
      new MetricResult(tree.pos, tree.name + "$object", "LCOMneg", lcomN))
  }

  override def run(tree: ClassDefinition, code: List[String]): List[MetricResult] = {
    val (lcom, lcomN) = getLCOM(tree, false)
    List(new MetricResult(tree.pos, tree.name + "$object", "LCOM", lcom),
      new MetricResult(tree.pos, tree.name + "$object", "LCOMneg", lcomN))
  }

  override def run(tree: TraitDefinition, code: List[String]): List[MetricResult] = {
    val (lcom, lcomN) = getLCOM(tree, false)
    List(new MetricResult(tree.pos, tree.name + "$object", "LCOM", lcom),
      new MetricResult(tree.pos, tree.name + "$object", "LCOMneg", lcomN))
  }


  def getLCOM(tree: Module, nested: Boolean): (Int, Int) = {
    val pairs = getPairsFunction(tree, nested)
    val instanceValues = getVariables(tree)
    val c = pairs.foldLeft(List[Boolean]()){
      (a, x) =>
        getUsedVariables(x._1, nested).intersect(getUsedVariables(x._2, nested)).exists(x => instanceValues.contains(x)) :: a
    }
    val p = c.count(x => !x)
    val q = c.count(x => x)
    val lcom = if (p - q > 0) p - q else 0
    (lcom, p - q)
  }


  def getVariables(tree: Module): List[String] = {
    tree.children.foldLeft(List[String]()) {
      (a, b) =>
        b match {
          case x: ValueDefinition =>
            x.name :: a
          case _ =>
            a
        }
    }
  }

  def getFunctions(tree: Module, nested: Boolean) : List[FunctionDef] = {
    def recursive(ast: AST) : List[FunctionDef] = ast match {
      case x: FunctionDef =>
        if (x.name == "<init>")
          List()
        else if (nested)
          x :: x.children.foldLeft(List[FunctionDef]())(_ ::: recursive(_))
        else
          List(x)
      case x: Module =>
        List()
      case _ =>
        if (nested)
          ast.children.foldLeft(List[FunctionDef]())(_ ::: recursive(_))
        else
          List()
    }
    tree.children.foldLeft(List[FunctionDef]())(_ ::: recursive(_))
  }

  def getPairsFunction(tree: Module, nested: Boolean): List[(FunctionDef, FunctionDef)] = {
    @tailrec
    def recursive(tree1: List[FunctionDef], tree2: List[FunctionDef], runningResult: List[(FunctionDef, FunctionDef)] = List()) : List[(FunctionDef, FunctionDef)] = (tree1, tree2) match {
      case (Nil, Nil) =>
        runningResult
      case ((x::Nil), Nil) =>
        runningResult
      case ((x::tail), Nil) =>
        recursive(tail, tail.tail, runningResult)
      case (x, (y::tail)) =>
        recursive(x, tail,(x.head, y) :: runningResult)
    }

    val functions = getFunctions(tree, nested)
    if (functions.nonEmpty)
      recursive(functions, functions.tail)
    else
      List()
  }

  def getUsedVariables(tree: AST, nested: Boolean) : List[String] = {
    tree.children.foldLeft(List[String]()) {
      (a, b) =>
        b match {
          case x: Value =>
              x.name :: a ::: getUsedVariables(b, nested)
          case x: FunctionDef =>
            if (nested)
              a ::: getUsedVariables(b, nested)
            else
              a
          case _ =>
            a ::: getUsedVariables(b, nested)
        }
    }
  }
}
