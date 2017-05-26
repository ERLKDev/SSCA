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
}
