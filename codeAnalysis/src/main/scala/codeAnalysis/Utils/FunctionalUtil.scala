package codeAnalysis.Utils

import codeAnalysis.analyser.AST._

/**
  * Created by Erik on 14-4-2017.
  */
trait FunctionalUtil {
  private val functionalFuncs = List("foldLeft", "foldRight", "fold", "map", "filter", "count", "exist", "find")
  private val impFuncs = List("foreach")

  /**
    * Checks whether the function is recursive or not
    *
    * @param tree the ast
    * @return
    */
  def isRecursive(tree: AST): Boolean ={
    def recursive(tree: AST, functionName: String) : Boolean = tree match {
      case node: FunctionCall =>
        if(node.owner + "." + node.name == functionName)
          true
        else
          tree.children.exists(x => recursive(x, functionName))
      case _ =>
        tree.children.exists(x => recursive(x, functionName))
    }

    tree match {
      case x: FunctionDef =>
        recursive(x, x.owner + "." + x.name)
      case _ =>
        false
    }
  }

  /**
    * Counts the amount of side effects in a tree (var's)
    *
    * @param tree the ast
    * @return
    */
  def countSideEffects(tree: AST) : Int = tree match {
    case (_: Var) | (_: VarAssignment) | (_: VarDefinition) =>
      1
    case _ =>
      tree.children.foldLeft(0)((a, b) => a + countSideEffects(b))
  }


  /**
    * Checks if a function is nested
    * @param tree
    * @return
    */
  def isNested(tree: FunctionDef): Boolean = {
    tree.nested
  }

  /**
    * Counts the number of functional function calls
    *
    * @param tree
    * @return (functional, imperative)
    */
  def countFuncCalls(tree: AST): (Int, Int) = tree match {
    case node: FunctionCall =>
      if (functionalFuncs.contains(node.name))
        tree.children.foldLeft((0, 0)){(a, b) => val res = countFuncCalls(b); (a._1 + res._1 + 1, a._2 + res._2) }
      else if (impFuncs.contains(node.name))
        tree.children.foldLeft((0, 0)){(a, b) => val res = countFuncCalls(b); (a._1 + res._1, a._2 + res._2 + 1) }
      else
        tree.children.foldLeft((0, 0)){(a, b) => val res = countFuncCalls(b); (a._1 + res._1, a._2 + res._2) }
    case x: MatchCase =>
      tree.children.foldLeft((0, 0)){(a, b) => val res = countFuncCalls(b); (a._1 + res._1 + 1, a._2 + res._2) }
    case x: For =>
      tree.children.foldLeft((0, 0)){(a, b) => val res = countFuncCalls(b); (a._1 + res._1, a._2 + res._2 + 1) }
    case x: While =>
      tree.children.foldLeft((0, 0)){(a, b) => val res = countFuncCalls(b); (a._1 + res._1, a._2 + res._2 + 1) }
    case x: DoWhile =>
      tree.children.foldLeft((0, 0)){(a, b) => val res = countFuncCalls(b); (a._1 + res._1, a._2 + res._2 + 1) }
    case _ =>
      tree.children.foldLeft((0, 0)){(a, b) => val res = countFuncCalls(b); (a._1 + res._1, a._2 + res._2) }
  }

  /**
    * Count the amount of higher order functions in the params or return type of a function
    *
    * @param tree the function
    * @return
    */
  def countHigherOrderParams(tree: FunctionDef) : Int = {
    def recursive(params: List[Param]) : Int = params match {
      case Nil =>
        0
      case x :: tail =>
        if (x.higher)
          1 + recursive(tail)
        else
          0 + recursive(tail)
      case _ =>
        0
    }
    recursive(tree.params)
  }

  def functionalScore(tree: FunctionDef) : Double = {
    val recursive = if (isRecursive(tree)) 1 else 0
    val nested = if (isNested(tree)) 1 else 0
    val (func, imp) = countFuncCalls(tree)

    val funcPoints = recursive + nested + func + countHigherOrderParams(tree)
    val impPoints = imp + countSideEffects(tree)

    if (impPoints + funcPoints > 0)
      funcPoints.toDouble / (impPoints.toDouble + funcPoints.toDouble)
    else
      0
  }
}
