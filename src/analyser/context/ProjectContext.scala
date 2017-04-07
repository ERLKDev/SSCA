package analyser.context

import analyser.Compiler.CompilerProvider

/**
  * Created by ErikL on 4/7/2017.
  */
class ProjectContext extends CompilerProvider{
  import global._

  var tree: Array[Tree] = _
  var code: List[String] = _
  var objects: List[ObjectInfo] = List[ObjectInfo]()
  var functions: List[FunctionInfo] = List[FunctionInfo]()

  def init(tree: Array[Tree], code: List[String]): Unit = {
    this.tree = tree
    this.code = code
  }

  def getProjectTree: Array[Tree] = tree
  def getProjectCode: List[String] = code

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
}
