package main.scala.Utils

import analyser.AST._

/**
  * Created by ErikL on 4/6/2017.
  */
trait ComplexUtil {

  /**
    * Function to measure the cyclomatic Complexity (CC)
    *
    * @param tree the ast tree
    * @return
    */
  def measureComplexity(tree: AST): Int = {
    def recursive(tree: AST): Int = tree match {
      case x: For =>
        tree.children.foldLeft(1)((a, b) => a + recursive(b))
      case x: IfStatement =>
        tree.children.foldLeft(1 /*+ getLogicalAndOr(x.cond)*/)((a, b) => a + recursive(b))
      case x: Case =>
        tree.children.foldLeft(/*getCaseAlternatives(x.pat)*/0)((a, b) => a + recursive(b))
      case _ =>
        tree.children.foldLeft(0)((a, b) => a + recursive(b))
    }

    1 + recursive(tree)
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
