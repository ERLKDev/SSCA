package main.scala.analyser.util

import main.scala.analyser.Compiler.CompilerProvider

import scala.reflect.internal.util.{OffsetPosition, RangePosition}

/**
  * Created by Erik on 5-4-2017.
  */
trait TreeUtil extends CompilerProvider{
import global._

  /**
    * Function to get the name of a DefDef
    *
    * @param tree The ast
    * @return
    */
  def getName(tree: DefDef) : String = {
    tree.name.toString
  }

  /**
    * Function to get the name of a ModuleDef
    *
    * @param tree The ast
    * @return
    */
  def getName(tree: ModuleDef) : String = {
    tree.name.toString
  }

  /**
    * Function to get the name of a ClassDef
    *
    * @param tree The ast
    * @return
    */
  def getName(tree: ClassDef) : String = {
    tree.name.toString
  }

  /**
    * Function to get the owner
    *
    * @param owner The owner symbol of a tree
    * @return
    */
  def getOwner(owner: Symbol) : String = {
    getPackage(owner) + owner.name.toString
  }

  /**
    * Function to get the package string of a object
    *
    * @param symbol The symbol of a tree
    * @return
    */
  def getObjectPackage(symbol: Symbol): String = {
    val pack = getPackage(symbol)
    val (result, _) = pack.splitAt(pack.length - 1)
    result
  }


  /**
    * Function to get the package string
    *
    * @param symbol The symbol of a tree
    * @return
    */
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


  /**
    * Function to get the range position of a tree
    *
    * @param tree The tree
    * @return
    */
  def getRangePos(tree : Tree): RangePosition = {
    tree.pos match {
      case position: RangePosition =>
        position
      case position: OffsetPosition =>
        new RangePosition(position.source, position.point, position.point, position.point)
      case _ =>
        null
    }
  }


  /**
    * Function to get the original source code of a tree
    *
    * @param tree The tree
    * @return
    */
  def getOriginalSourceCode(tree : Tree): List[String] = {
    val pos = getRangePos(tree)
    if (pos == null)
      return null
    tree.pos.source.content.array.subSequence(pos.start, pos.end).toString.split("\n").toList

  }
}
