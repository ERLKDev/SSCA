package codeAnalysis

/**
  * Created by Erik on 13-4-2017.
  */
class Test {

  trait B {

  }

  def test(node: AST): Unit ={
    koe(x => true, "kees")
  }

  def koe(f: (String) => Boolean, a: String) : Boolean= {
    println("test")
    f(a)
  }
}
