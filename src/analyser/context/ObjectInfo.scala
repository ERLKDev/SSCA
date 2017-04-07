package analyser.context

import analyser.Compiler.CompilerProvider
import analyser.util.TreeUtil

/**
  * Created by ErikL on 4/7/2017.
  */
class ObjectInfo extends CompilerProvider with TreeUtil{
  import global._
  private var name: String = _
  private var parents: List[String] = _
  private var pos: Position = _
  private var isTraitB: Boolean = false
  private var isObjectB: Boolean = false
  private var isClassB: Boolean = false

  def init(tree: ClassDef): Unit = {
    pos = getRangePos(tree)
    name = getName(tree)
    parents = checkExtends(tree)
    isTraitB = tree.symbol.isTrait
    isClassB = !tree.symbol.isTrait
  }

  def init(tree: ModuleDef): Unit = {
    pos = getRangePos(tree)
    name = getName(tree)
    parents = checkExtends(tree)
    isObjectB = true
  }

  def getPos: Position = pos
  def getName: String = name
  def getParents: List[String] = parents
  def isTrait: Boolean = isTraitB
  def isObject: Boolean = isObjectB
  def isClass: Boolean = isClassB



  private def checkExtends(tree: ClassDef): List[String] = {
    tree.impl.parents.foldLeft(List[String]())((a,b) => getParent(b) :: a)
  }

  private def checkExtends(tree: ModuleDef): List[String] = {
    tree.impl.parents.foldLeft(List[String]())((a,b) => getParent(b) :: a)
  }

  private def getParent(x: Tree) : String = x match {
    case y: TypeTree =>
      y.tpe.toString()
    case Select(_, y) =>
      getPackage(x.symbol) + y.toString
    case Ident(y) =>
      getPackage(x.symbol) + y.toString
  }
}
