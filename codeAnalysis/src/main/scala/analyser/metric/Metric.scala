package main.scala.analyser.metric

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.util.TreeUtil

/**
  * Created by Erik on 5-4-2017.
  */
trait Metric extends CompilerProvider with TreeUtil{
  var projectContext : ProjectContext  = _

  def init(projectContext: ProjectContext): Unit = {
    this.projectContext = projectContext
  }

  def getContext: ProjectContext = projectContext
}
