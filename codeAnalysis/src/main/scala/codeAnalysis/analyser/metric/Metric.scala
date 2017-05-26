package main.scala.analyser.metric

import main.scala.analyser.context.ProjectContext

/**
  * Created by Erik on 5-4-2017.
  */
trait Metric {
  private var projectContext : ProjectContext  = _

  /**
    * Function to initialize the metric
    *
    * @param projectContext
    */
  def init(projectContext: ProjectContext): Unit = {
    this.projectContext = projectContext
  }

  /**
    * Returns the project context
    * @return
    */
  def getContext: ProjectContext = projectContext

  /**
    * Function to create a new instance of the metric
    * @return
    */
  def newInstance(): Metric = this.getClass.newInstance()
}
