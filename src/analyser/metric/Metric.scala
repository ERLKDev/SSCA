package analyser.metric

import analyser.Compiler.CompilerProvider
import analyser.util.TreeUtil

/**
  * Created by Erik on 5-4-2017.
  */
trait Metric extends CompilerProvider with TreeUtil{
  import global._
  var projectTree : Array[Tree] = _
  var projectCode : List[String]  = _

  def init(projectTree: Array[Tree], projectCode: List[String]): Unit = {
    this.projectTree = projectTree
    this.projectCode = projectCode
  }

  def getProjectTree: Array[global.Tree] = {
    projectTree
  }

  def getProjectCode: List[String] = {
    projectCode
  }
}
