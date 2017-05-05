package main.scala.Utils

import codeAnalysis.analyser.AST._

/**
  * Created by ErikL on 4/6/2017.
  */
trait ComplexUtil {


  def measureComplexity(tree: AST): Int = measureComplexity(tree, true)

  /**
    * Function to measure the cyclomatic Complexity (CC)
    *
    * @param tree the ast tree
    * @return
    */
  def measureComplexity(tree: AST, nested: Boolean): Int = {
    def recursive(tree: AST): Int = tree match {
      case x: For =>
        tree.children.foldLeft(1)((a, b) => a + recursive(b))
      case x: IfStatement =>
        tree.children.foldLeft(1)((a, b) => a + recursive(b))
      case x: Case =>
        tree.children.foldLeft(1)((a, b) => a + recursive(b))
      case x: FunctionDef =>
        if (!nested)
          0
        else
          tree.children.foldLeft(0)((a, b) => a + recursive(b))
      case _ =>
        tree.children.foldLeft(0)((a, b) => a + recursive(b))
    }

    tree.children.foldLeft(1)((a, b) => a + recursive(b))
  }


/*  def getCaseAlternatives(tree: AST): Int = tree match {
    case x: Alternative =>
      x.length
    case _ =>
      1
  }*/


/*  def getLogicalAndOr(tree: AST): Int = tree match {
    case x: Select =>
      if (x.name.toString == "$amp$amp")
        return tree.children.foldLeft(1)((a,b) => a + getLogicalAndOr(b))

      if (x.name.toString == "$bar$bar")
        return tree.children.foldLeft(1)((a,b) => a + getLogicalAndOr(b))

      tree.children.foldLeft(0)((a,b) => a + getLogicalAndOr(b))
    case _ =>
      tree.children.foldLeft(0)((a,b) => a + getLogicalAndOr(b))
  }*/


}
