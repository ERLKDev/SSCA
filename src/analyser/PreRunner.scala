package analyser

import analyser.Compiler.CompilerProvider
import analyser.context.ProjectContext
import analyser.util.TreeUtil

/**
  * Created by ErikL on 4/7/2017.
  */
object PreRunner extends CompilerProvider with TreeUtil{
  import global._

  def run(trees: Array[Tree]) : ProjectContext = {
    val projectContext = new ProjectContext
    projectContext.init(trees.asInstanceOf[Array[projectContext.global.Tree]], getOriginalSourceCode(trees))

    def recursive(tree: Tree): Unit = tree match {
      case x: ModuleDef =>
        projectContext.addObjectInfo(x.asInstanceOf[projectContext.global.ModuleDef])
        tree.children.foreach(recursive)
      case x: ClassDef =>
        projectContext.addObjectInfo(x.asInstanceOf[projectContext.global.ClassDef])
        tree.children.foreach(recursive)
      case x: DefDef =>
        projectContext.addFunctionInfo(x.asInstanceOf[projectContext.global.DefDef])
        tree.children.foreach(recursive)
      case _ =>
        tree.children.foreach(recursive)
    }
    trees.foreach(recursive)
    projectContext
  }
}
