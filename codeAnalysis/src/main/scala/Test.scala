package main.scala

import main.scala.analyser.util.TreeSyntaxUtil

/**
  * Created by Erik on 13-4-2017.
  */
class Test extends TreeSyntaxUtil {
  def test(node: AstNode): Unit ={
    koe(x => true, "kees")
  }

  def koe(f: (String) => Boolean, a: String) : Boolean= {
    println("test")
    f(a)
  }
}
