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
  val metricRunner = new MetricRunner

  var projectFiles: List[File] = _
  var projectContext: ProjectContext = _

  refresh()

  def refresh(): Unit = {
    projectFiles = getProjectFiles(projectPath).toList
    projectContext = new ProjectContext(projectFiles)
  }

  def analyse(path: String): Result = {
    global.ask { () =>
      val a = new File(path)
      metricRunner.run(metrics, List(a), projectContext)
    }
  }

  def analyse(): Result  = {
    global.ask { () =>
      metricRunner.run(metrics, projectFiles, projectContext)
    }
  }
}

