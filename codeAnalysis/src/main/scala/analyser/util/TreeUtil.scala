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
    getPackage(tree.symbol) + tree.name.toString + "$Object"
  }

  def getName(tree: ClassDef) : String = {
    getPackage(tree.symbol) + tree.name.toString + (if (tree.symbol.isTrait) "$Trait" else "$Class")
  }

  def getPackage(symbol: Symbol) : String = symbol.owner match {
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

  def isTrait(tree: Tree): Boolean = tree match {
    case x: ClassDef =>
      x.mods.isTrait
    case _ =>
      false
  }

  def isClass(tree: Tree): Boolean = tree match  {
    case x: ModuleDef =>
      !x.mods.isTrait
    case _ =>
      false
  }

  def isObject(tree: Tree): Boolean = tree match {
    case _: ModuleDef =>
      true
    case _ =>
      false
  }

  def isFunction(tree: Tree): Boolean = tree match {
    case _: DefDef =>
      true
    case _ =>
      false
  }

  def isAnonymousFunction(tree: Tree): Boolean = tree match {
    case x: Function =>
      x.symbol.isAnonymousFunction
    case _ =>
      false
  }

  def isFunctionCall(tree: Tree): Boolean = tree match {
    case x: Apply =>
      true
    case _ =>
      false
  }

  def isAssignment(tree: Tree): Boolean = tree match  {
    case _: Assign =>
      true
    case _ =>
      false
  }

  def isValDef(tree: Tree): Boolean = tree match  {
    case x: ValDef =>
      !x.symbol.isMutable
    case _ =>
      false
  }

  def isVarDef(tree: Tree): Boolean = tree match {
    case x: ValDef =>
      x.symbol.isMutable
    case _ =>
      false
  }

  def isVar(tree: Tree): Boolean = tree match {
    case x: Ident =>
      x.symbol.isVar
    case _ =>
      false
  }

  def isVal(tree: Tree): Boolean = tree match {
    case x: Ident =>
      x.symbol.isVal
    case _ =>
      false
  }

  def isFor(tree: Tree): Boolean = tree match {
    case x: Apply =>
      x.fun.symbol.name.toString == "foreach"
    case _ =>
      false
  }
}
