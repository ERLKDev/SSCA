package main.scala.analyser.util

import main.scala.analyser.Compiler.CompilerProvider

import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
trait TreeUtil extends CompilerProvider{
import global._

  def getName(tree: DefDef) : String = {
    tree.name.toString
  }

  def getName(tree: ModuleDef) : String = {
    tree.name.toString
  }

  def getName(tree: ClassDef) : String = {
    tree.name.toString
  }

  def getOwner(owner: Symbol) : String = {
    getPackage(owner) + owner.name.toString
  }

  def getObjectPackage(symbol: Symbol): String = {
    val pack = getPackage(symbol)
    val (result, _) = pack.splitAt(pack.length - 1)
    result
  }

  private def getPackage(symbol: Symbol) : String = symbol.owner match {
    case x: PackageClassSymbol =>
      if (x.isRoot || x.isEmptyPackageClass) {
        ""
      }else {
        getPackage(x) + x.nameString + "."
      }
    case _ =>
      ""
  }

  def getPosition(tree : Tree): Position = {
    tree.pos
  }

  def getRangePos(tree : Tree): RangePosition = {
    tree.pos match {
      case position: RangePosition =>
        position
      case _ =>
        null
    }
  }

  def getOriginalSourceCode(tree : Tree): List[String] = {
    val pos = getRangePos(tree)
    if (pos == null)
      return null
    tree.pos.source.content.array.subSequence(pos.start, pos.end).toString.split("\n").toList

  }

  def getOriginalSourceCode(tree : Array[Tree]): List[String]  = {
    tree.foldLeft(List[String]())((a, b) => a ++ getOriginalSourceCode(b))
  }
}
