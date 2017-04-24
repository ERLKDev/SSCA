package analyser.Compiler

/**
  * Created by erikl on 4/24/2017.
  */
class TreeWrapper(val compiler: CompilerS) {
  private var tree: compiler.global.Tree = _

  def wrap(tree: compiler.global.Tree): TreeWrapper = {
    this.tree = tree
    this
  }

  def unWrap(): compiler.global.Tree = {
    tree
  }
}
