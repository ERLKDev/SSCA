package main.scala.Utils

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.util.TreeSyntaxUtil

/**
  * Created by ErikL on 4/6/2017.
  */
trait ComplexUtil extends CompilerProvider  with TreeSyntaxUtil{
  import global._


  /**
    * Function to measure the cyclomatic Complexity (CC)
    *
    * @param tree the ast tree
    * @return
    */
  def measureComplexity(tree: Tree): Int = {
    def recursive(tree: Tree): Int = getAstNode(tree) match {
      case For(x) =>
        tree.children.foldLeft(1)((a, b) => a + recursive(b))
      case IfStatement(x) =>
        tree.children.foldLeft(1 + getLogicalAndOr(x.cond))((a, b) => a + recursive(b))
      case Case(x) =>
        tree.children.foldLeft(getCaseAlternatives(x.pat))((a, b) => a + recursive(b))
      case _ =>
        tree.children.foldLeft(0)((a, b) => a + recursive(b))
    }

    1 + recursive(tree)
  }

  /**
    * Checks whether there are alternative case conditions
    *
    * @param tree the ast
    * @return
    */
  def getCaseAlternatives(tree: Tree): Int = tree match {
    case Alternative(x) =>
      x.length
    case _ =>
      1
  }

  /**
    * Checks whether a logical "and"(&&) or "or"(||) exists in the tree
    *
    * @param tree the ast
    * @return
    */
  def getLogicalAndOr(tree: Tree): Int = tree match {
    case x: Select =>
      if (x.name.toString == "$amp$amp")
        return tree.children.foldLeft(1)((a,b) => a + getLogicalAndOr(b))

      if (x.name.toString == "$bar$bar")
        return tree.children.foldLeft(1)((a,b) => a + getLogicalAndOr(b))

      tree.children.foldLeft(0)((a,b) => a + getLogicalAndOr(b))
    case _ =>
      tree.children.foldLeft(0)((a,b) => a + getLogicalAndOr(b))
  }


}
