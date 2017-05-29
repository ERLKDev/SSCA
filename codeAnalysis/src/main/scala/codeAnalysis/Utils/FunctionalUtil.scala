package codeAnalysis.Utils

import codeAnalysis.analyser.AST._

/**
  * Created by Erik on 14-4-2017.
  */
trait FunctionalUtil {
  private val functionalFuncs = List("foldLeft", "foldRight", "fold", "map", "filter", "count", "exist", "find")

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
    * @return
    */
  def countFunctionalFuncCalls(tree: AST): Int = tree match {
    case node: FunctionCall =>
      if (functionalFuncs.contains(node.name))
        tree.children.foldLeft(1)((a, b) => a + countFunctionalFuncCalls(b))
      else
        tree.children.foldLeft(0)((a, b) => a + countFunctionalFuncCalls(b))
    case _ =>
      tree.children.foldLeft(0)((a, b) => a + countFunctionalFuncCalls(b))
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
    def count(tree: AST) : Int = tree match {
      case _ =>
        tree.children.foldLeft(1)((a,b) => a + count(b))
    }

    if (countSideEffects(tree) > 0)
      return 0.0

    val recursiveScore = if (isRecursive(tree)) 0.125 else 0.0
    val nestedScore = if (isNested(tree)) 0.125 else 0.0
    val higherOrderScore : Double = 0.125 * countHigherOrderParams(tree).toDouble / (tree.params.length + 1)

    val score = 0.5 + recursiveScore + nestedScore + higherOrderScore + (countFunctionalFuncCalls(tree) / count(tree).toDouble)

    if (score > 1.0)
      1.0
    else
      score

    //0.25 * recursiveScore + 0.25 * nestedScore + 0.25 * higherOrderScore + (countFunctionalFuncCalls(tree) / count(tree).toDouble)

  }
}
