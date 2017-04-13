package main.scala.analyser.metric

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.util.TreeUtil

/**
  * Created by Erik on 5-4-2017.
  */
trait Metric extends CompilerProvider with TreeUtil{
  import global._
  var projectContext : ProjectContext  = projectContext

  def init(projectContext: ProjectContext): Unit = {
    this.projectContext = projectContext
  }

  def getProjectTree: Array[Tree] = {
    projectContext.getProjectTree.asInstanceOf[Array[Tree]]
  }

  def getProjectCode: List[String] = {
    projectContext.getProjectCode
  }

  def getContext: ProjectContext = projectContext
}
