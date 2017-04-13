package main.scala.analyser

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.util.TreeUtil

/**
  * Created by ErikL on 4/7/2017.
  */
object PreRunner extends CompilerProvider with TreeUtil{
  import global._

  def run(trees: Array[Tree]) : ProjectContext = {
    val projectContext = new ProjectContext

    global.ask { () =>
      def recursiveInit(tree: Tree): Unit = tree match {
        case x: ModuleDef =>
          if (!x.symbol.isAnonymousClass)
            projectContext.addObjectInfo(x.asInstanceOf[projectContext.global.ModuleDef])
          tree.children.foreach(recursiveInit)
        case x: ClassDef =>
          if (!x.symbol.isAnonymousClass)
            projectContext.addObjectInfo(x.asInstanceOf[projectContext.global.ClassDef])
          tree.children.foreach(recursiveInit)
        case x: DefDef =>
          if (!x.symbol.isAnonymousFunction)
            projectContext.addFunctionInfo(x.asInstanceOf[projectContext.global.DefDef])
          tree.children.foreach(recursiveInit)
        case _ =>
          tree.children.foreach(recursiveInit)
      }

      projectContext.init(trees.asInstanceOf[Array[projectContext.global.Tree]])
      //trees.foreach(recursiveInit)

    }
    projectContext
  }

}
