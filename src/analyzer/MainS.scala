package analyzer

import analyzer.Compiler.CompilerProvider

/**
  * Created by Erik on 5-4-2017.
  */
object MainS extends CompilerProvider {
  import global._

  def main (args: Array[String] ): Unit = {
    val a = "D:\\Master project 2017\\testproj\\src\\Test.scala"
    val b = treeFromFile(a)
    println(showRaw(b))
  }
}
