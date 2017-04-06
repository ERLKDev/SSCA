package Utils

import analyser.Compiler.CompilerProvider

/**
  * Created by ErikL on 4/6/2017.
  */
trait ComplexUtil extends CompilerProvider{
  import global._


  def measureComplexity(tree: Tree): Int = {

    def recursive(tree: Tree): Int = tree match {
      case Select(_, x) => {
        x.toString match {
          case "foreach" =>
            1
          case _ =>
            0
        }
      }
      case Match(_, x) =>
        tree.children.foldLeft(x.size)((a, b) => a + recursive(b))
      case _: If =>
        tree.children.foldLeft(1)((a, b) => a + recursive(b))
      case _ =>
        tree.children.foldLeft(0)((a, b) => a + recursive(b))
    }

    1 + recursive(tree)
  }

}
