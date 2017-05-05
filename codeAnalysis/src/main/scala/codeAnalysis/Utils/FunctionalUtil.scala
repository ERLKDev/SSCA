package codeAnalysis.Utils

import codeAnalysis.analyser.AST._

/**
  * Created by Erik on 14-4-2017.
  */
trait FunctionalUtil {
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
    * Counts the ammount of side effects in a tree (var's)
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
}
