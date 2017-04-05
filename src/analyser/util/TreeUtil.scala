package analyser.util

import analyser.Compiler.CompilerProvider

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

  def isTrait(tree: ClassDef): Boolean = {
    tree.mods.isTrait
  }
}
