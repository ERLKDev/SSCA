package main.scala

import main.scala.analyser.util.TreeSyntaxUtil

/**
  * Created by Erik on 13-4-2017.
  */
class Test extends TreeSyntaxUtil {
  def test(): Unit = {
    val a: AstNode = IfStatement(null)

    a match {
      case IfStatement(_) =>
        println("a")
      case _ =>
        println("b")
    }
  }
}
