package main.scala.analyser.util

import codeAnalysis.analyser.AST.{ClassParent, Parent, TraitParent}
import codeAnalysis.analyser.Compiler.CompilerS

import scala.reflect.internal.util.{OffsetPosition, RangePosition}

/**
  * Created by Erik on 5-4-2017.
  */
class TreeUtil(val compiler: CompilerS){
  import compiler.global._

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
    * Function to get the name of a ClassDef
    *
    * @param tree The ast
    * @return
    */
  def getName(tree: Tree) : String = {
    tree.symbol.nameString
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


  def getParents(x: Symbol) : List[Parent] = {
    def recursive(y: Symbol) : List[Parent] = {
      val name = getPackage(y) + y.nameString
      if (name == "java.lang.Object" || name == "scala.Any")
        List()
      else if (y.isClass && !y.isTrait)
        List(ClassParent(y.nameString, getPackage(y), getParents(y)))
      else if (y.isClass && y.isTrait)
        List(TraitParent(y.nameString, getPackage(y), getParents(y)))
      else
        List()
    }

    x.parentSymbols.foldLeft(List[Parent]())((a, b) => a ::: recursive(b))
  }
}
