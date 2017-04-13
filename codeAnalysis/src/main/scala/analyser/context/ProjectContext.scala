package main.scala.analyser.context

import main.scala.analyser.Compiler.CompilerProvider

/**
  * Created by ErikL on 4/7/2017.
  */
class ProjectContext extends CompilerProvider{
  import global._

  var tree: Array[Tree] = _
  var objects: List[ObjectInfo] = List[ObjectInfo]()
  var functions: List[FunctionInfo] = List[FunctionInfo]()

  def init(tree: Array[Tree]): Unit = {
    this.tree = tree
  }

  def getProjectTree: Array[Tree] = tree

  def addObjectInfo(tree: ClassDef): Unit = {
    val obj = new ObjectInfo()
    obj.init(tree.asInstanceOf[obj.global.ClassDef])
    objects = objects ::: List(obj)
  }

  def addObjectInfo(tree: ModuleDef): Unit = {
    val obj = new ObjectInfo()
    obj.init(tree.asInstanceOf[obj.global.ModuleDef])
    objects = objects ::: List(obj)

  }

  def addFunctionInfo(tree: DefDef): Unit = {
    val func = new FunctionInfo()
    func.init(tree.asInstanceOf[func.global.DefDef])
    functions = functions ::: List(func)
  }

  def getObjectByName(name: String) : ObjectInfo = {
    def recursive(objects: List[ObjectInfo]) : ObjectInfo = objects match {
      case Nil =>
        null
      case x::tail =>
        if (x.getName.equals(name)) x else recursive(tail)
    }

    recursive(objects)
  }
}
