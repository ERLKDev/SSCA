package main.scala.Utils

import analyser.Compiler.TreeWrapper
import main.scala.analyser.util.TreeSyntaxUtil

/**
  * Created by ErikL on 4/6/2017.
  */
trait ComplexUtil extends TreeSyntaxUtil{
  /**
    * Function to measure the cyclomatic Complexity (CC)
    *
    * @param wrappedTree the ast tree
    * @return
    */
  def measureComplexity(wrappedTree: TreeWrapper): Int = {
    import wrappedTree.compiler.global._

    def recursive(wrappedTree: TreeWrapper): Int = getAstNode(wrappedTree) match {
      case For(x) =>
        wrappedTree.unWrap().children.foldLeft(1)((a, b) => a + recursive(new TreeWrapper(wrappedTree.compiler).wrap(b)))
      case IfStatement(x) =>
        val y = x.unWrap().asInstanceOf[If]
        wrappedTree.unWrap().children.foldLeft(1 + getLogicalAndOr(new TreeWrapper(wrappedTree.compiler).wrap(y.cond))){
          (a, b) =>
            a + recursive(new TreeWrapper(wrappedTree.compiler).wrap(b))
        }
      case Case(x) =>
        val y = x.unWrap().asInstanceOf[CaseDef]
        wrappedTree.unWrap().children.foldLeft(getCaseAlternatives(new TreeWrapper(wrappedTree.compiler).wrap(y.pat))){
          (a, b) =>
            a + recursive(new TreeWrapper(wrappedTree.compiler).wrap(b))
        }
      case _ =>
        wrappedTree.unWrap().children.foldLeft(0)((a, b) => a + recursive(new TreeWrapper(wrappedTree.compiler).wrap(b)))
    }

    1 + recursive(wrappedTree)
  }

  /**
    * Checks whether there are alternative case conditions
    *
    * @param wrappedTree the ast
    * @return
    */
  def getCaseAlternatives(wrappedTree: TreeWrapper): Int = wrappedTree.unWrap() match {
    case wrappedTree.compiler.global.Alternative(x) =>
      x.length
    case _ =>
      1
  }

  /**
    * Checks whether a logical "and"(&&) or "or"(||) exists in the tree
    *
    * @param wrappedTree the ast
    * @return
    */
  def getLogicalAndOr(wrappedTree: TreeWrapper): Int = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.Select =>
      if (x.name.toString == "$amp$amp")
        return wrappedTree.unWrap().children.foldLeft(1)((a,b) => a + getLogicalAndOr(new TreeWrapper(wrappedTree.compiler).wrap(b)))

      if (x.name.toString == "$bar$bar")
        return wrappedTree.unWrap().children.foldLeft(1)((a,b) => a + getLogicalAndOr(new TreeWrapper(wrappedTree.compiler).wrap(b)))

      wrappedTree.unWrap().children.foldLeft(0)((a,b) => a + getLogicalAndOr(new TreeWrapper(wrappedTree.compiler).wrap(b)))
    case _ =>
      wrappedTree.unWrap().children.foldLeft(0)((a,b) => a + getLogicalAndOr(new TreeWrapper(wrappedTree.compiler).wrap(b)))
  }


}
