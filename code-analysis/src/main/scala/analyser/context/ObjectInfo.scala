package main.scala.analyser.context

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.util.TreeUtil

/**
  * Created by ErikL on 4/7/2017.
  */
class ObjectInfo extends CompilerProvider with TreeUtil{
  import global._
  private var name: String = _
  private var parents: List[Tree] = _
  private var pos: Position = _
  private var isTraitB: Boolean = false
  private var isObjectB: Boolean = false
  private var isClassB: Boolean = false

  def init(tree: ClassDef): Unit = {
    pos = getRangePos(tree)
    name = getName(tree)
    parents = tree.impl.parents
    isTraitB = tree.symbol.isTrait
    isClassB = !tree.symbol.isTrait
  }

  def init(tree: ModuleDef): Unit = {
    pos = getRangePos(tree)
    name = getName(tree)
    parents = tree.impl.parents
    isObjectB = true
  }

  def getPos: Position = pos
  def getName: String = name
  def getParents: List[Tree] = parents
  def isTrait: Boolean = isTraitB
  def isObject: Boolean = isObjectB
  def isClass: Boolean = isClassB
  def DIT: Int = countInherDepth()


  private def countInherDepth() : Int = {
    def recursive(x: Symbol) : Int = {
      x.parentSymbols.foldLeft(0){ (a, b) =>
        if (b.isClass && !b.isTraitOrInterface) {
          recursive(b) + 1
        }else
          a
      }
    }
    parents.foldLeft(1){
      (a, b) =>
        if (b.symbol.isClass && !b.symbol.isTraitOrInterface) {
          recursive(b.symbol)
        }else
          a
    }
  }
}
