package main.scala.analyser.metric

import main.scala.analyser.context.ProjectContext

/**
  * Created by Erik on 5-4-2017.
  */
trait Metric {
  var projectContext : ProjectContext  = _

  def init(projectContext: ProjectContext): Unit = {
    this.projectContext = projectContext
  }

  def getContext: ProjectContext = projectContext

  def newInstance(): Metric = this.getClass.newInstance()
}
