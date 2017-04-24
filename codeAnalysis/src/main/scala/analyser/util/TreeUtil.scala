package main.scala.analyser.util

import analyser.Compiler.{SymbolWrapper, TreeWrapper}

import scala.reflect.internal.util.{OffsetPosition, RangePosition}

/**
  * Created by Erik on 5-4-2017.
  */
trait TreeUtil{


  /**
    * Function to get the name of a DefDef
    *
    * @param wrappedTree The ast
    * @return
    */
  def getName(wrappedTree: TreeWrapper) : String = {
    wrappedTree.unWrap().symbol.name.toString
  }

  /**
    * Function to get the owner
    *
    * @param wrappedSymbol The owner symbol of a tree
    * @return
    */
  def getOwner(wrappedSymbol: SymbolWrapper) : String = {
    getPackage(wrappedSymbol) + wrappedSymbol.unWrap().name.toString
  }

  /**
    * Function to get the package string of a object
    *
    * @param wrappedSymbol The symbol of a tree
    * @return
    */
  def getObjectPackage(wrappedSymbol: SymbolWrapper): String = {
    val pack = getPackage(wrappedSymbol)
    val (result, _) = pack.splitAt(pack.length - 1)
    result
  }


  /**
    * Function to get the package string
    *
    * @param wrappedSymbol The symbol of a tree
    * @return
    */
  private def getPackage(wrappedSymbol: SymbolWrapper) : String = wrappedSymbol.unWrap().owner match {
    case x: wrappedSymbol.compiler.global.PackageClassSymbol =>
      if (x.isRoot || x.isEmptyPackageClass) {
        ""
      }else {
        getPackage(wrappedSymbol) + x.nameString + "."
      }
    case _ =>
      ""
  }


  /**
    * Function to get the range position of a tree
    *
    * @param wrappedTree The tree
    * @return
    */
  def getRangePos(wrappedTree : TreeWrapper): RangePosition = {
    wrappedTree.unWrap().pos match {
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
    * @param wrappedTree The tree
    * @return
    */
  def getOriginalSourceCode(wrappedTree : TreeWrapper): List[String] = {
    val pos = getRangePos(wrappedTree)
    if (pos == null)
      return null
    wrappedTree.unWrap().pos.source.content.array.subSequence(pos.start, pos.end).toString.split("\n").toList

  }
}
