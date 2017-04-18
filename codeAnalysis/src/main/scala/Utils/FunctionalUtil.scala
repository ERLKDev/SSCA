package Utils

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.util.TreeSyntaxUtil

/**
  * Created by Erik on 14-4-2017.
  */
trait FunctionalUtil extends CompilerProvider with TreeSyntaxUtil{
  import global._


  /**
    * Checks whether the function is recursive or not
    *
    * @param tree the ast
    * @return
    */
  def isRecursive(tree: Tree): Boolean ={
    def recursive(tree: Tree, functionName: String) : Boolean = getAstNode(tree) match {
      case FunctionCall(_, name, owner) =>
        if(owner + "." + name == functionName)
          true
        else
          tree.children.exists(x => recursive(x, functionName))
      case _ =>
        tree.children.exists(x => recursive(x, functionName))
    }

    getAstNode(tree) match {
      case x: FunctionDef =>
        recursive(x.tree, x.owner + "." + x.name)
      case x: NestedFunction =>
        recursive(x.tree, x.owner + "." + x.name)
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
  def countSideEffects(tree: Tree) : Int = getAstNode(tree) match {
    case (_: Var) | (_: VarAssignment) | (_: VarDefinition) =>
      1
    case _ =>
      tree.children.foldLeft(0)((a, b) => a + countSideEffects(b))
  }
}
