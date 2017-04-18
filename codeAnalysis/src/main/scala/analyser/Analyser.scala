package main.scala.analyser

import java.io.File

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.Metric
import main.scala.analyser.result.Result
import main.scala.analyser.util.ProjectUtil

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(projectPath: String, metrics : List[Metric]) extends CompilerProvider with ProjectUtil{
  private val metricRunner = new MetricRunner
  private var projectFiles: List[File] = _
  private var projectContext: ProjectContext = _

  /* Always refresh the context*/
  refresh()


  /**
    * Refreshes the context
    */
  def refresh(): Unit = {
    projectFiles = getProjectFiles(projectPath).toList
    projectContext = new ProjectContext(projectFiles)
  }

  /**
    * Analyse single file
    * @param path
    * @return result
    */
  def analyse(path: String): Result = {
    global.ask { () =>
      val a = new File(path)
      metricRunner.run(metrics, a, projectContext)
    }
  }

  /**
    * Analyse complete project
    * @return result
    */
  def analyse(): Result  = {
    global.ask { () =>
      metricRunner.runProject(metrics, projectFiles, projectContext)
    }
  }
}

