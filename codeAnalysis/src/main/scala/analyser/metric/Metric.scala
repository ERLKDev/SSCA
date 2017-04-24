package main.scala.analyser.metric

import analyser.Compiler.CompilerS
import main.scala.analyser.context.ProjectContext

/**
  * Created by Erik on 5-4-2017.
  */
trait Metric{
  var projectContext : ProjectContext  = _
  var compiler: CompilerS = _

  def init(projectContext: ProjectContext): Unit = {
    this.projectContext = projectContext
    this.compiler = compiler
  }

  def getContext: ProjectContext = projectContext
}
