package analyser.context

import analyser.Compiler.CompilerProvider
import analyser.util.TreeUtil

/**
  * Created by ErikL on 4/7/2017.
  */
class FunctionInfo extends CompilerProvider with TreeUtil{
  import global._

  private var name: String = _
  private var pos: Position = _

  def init(tree: DefDef): Unit = {
    name = getName(tree)
    pos = getRangePos(tree)
  }

  def getName: String = name
  def getPosition: Position = pos
}
