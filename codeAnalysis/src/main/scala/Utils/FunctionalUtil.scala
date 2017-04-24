package Utils

import analyser.Compiler.TreeWrapper
import main.scala.analyser.util.TreeSyntaxUtil

/**
  * Created by Erik on 14-4-2017.
  */
trait FunctionalUtil extends TreeSyntaxUtil{

  /**
    * Checks whether the function is recursive or not
    *
    * @param wrappedTree the ast
    * @return
    */
  def isRecursive(wrappedTree: TreeWrapper): Boolean ={
    def recursive(wrappedTree: TreeWrapper, functionName: String) : Boolean = getAstNode(wrappedTree) match {
      case FunctionCall(_, name, owner) =>
        if(owner + "." + name == functionName)
          true
        else
          wrappedTree.unWrap().children.exists(x => recursive(new TreeWrapper(wrappedTree.compiler).wrap(x), functionName))
      case _ =>
        wrappedTree.unWrap().children.exists(x => recursive(new TreeWrapper(wrappedTree.compiler).wrap(x), functionName))
    }

    getAstNode(wrappedTree) match {
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
    * @param wrappedTree the ast
    * @return
    */
  def countSideEffects(wrappedTree: TreeWrapper) : Int = getAstNode(wrappedTree) match {
    case (_: Var) | (_: VarAssignment) | (_: VarDefinition) =>
      1
    case _ =>
      wrappedTree.unWrap().children.foldLeft(0)((a, b) => a + countSideEffects(new TreeWrapper(wrappedTree.compiler).wrap(b)))
  }
}
