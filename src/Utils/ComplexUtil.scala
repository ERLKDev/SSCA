package Utils

import analyser.Compiler.CompilerProvider

/**
  * Created by ErikL on 4/6/2017.
  */
trait ComplexUtil extends CompilerProvider{
  import global._


  def measureComplexity(tree: Tree): Int = {
    var complex : Int = 1

    def recursive(tree: Tree): Unit = tree match {
      case Select(_, x) => {
        x.toString match {
          case "foreach" =>
            complex += 1
          case _ =>
        }
      }
      case Match(_, x) =>
        complex += x.size
        tree.children.foreach(x => recursive(x))
      case _: If =>
        complex += 1
        tree.children.foreach(x => recursive(x))
      case _ =>
        tree.children.foreach(x => recursive(x))
    }

    recursive(tree)
    complex
  }

}
