package main.scala.analyser

import java.io.File

import analyser.PreRunner
import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.Metric
import main.scala.analyser.prerun.PreRunJob
import main.scala.analyser.result.Result
import main.scala.analyser.util.ProjectUtil

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(projectPath: String, metrics : List[Metric]) extends CompilerProvider with ProjectUtil{
  private val preRunner = new PreRunner
  private val metricRunner = new MetricRunner

  private var projectFiles: List[File] = _
  private var projectContext: ProjectContext = _

  private val preRunJobs: List[PreRunJob] = metrics.filter(x => x.isInstanceOf[PreRunJob]).asInstanceOf[List[PreRunJob]]

  /* Always refresh the context*/
  refresh()


  /**
    * Refreshes the context
    */
  def refresh(): Unit = {
    projectFiles = getProjectFiles(projectPath).toList
    projectContext = new ProjectContext(projectFiles)

    /* Init main.scala.metrics*/
    metrics.foreach(f => f.init(projectContext))
  }

  /**
    * Analyse single file
    * @param path
    * @return result
    */
  def analyse(path: String): Result = {
    global.ask { () =>
      val file = new File(path)
      preRunner.run(preRunJobs, List(file))
      metricRunner.run(metrics, file, projectContext)
    }
  }

  /**
    * Analyse single file
    * @param paths
    * @return result
    */
  def analyse(paths: List[String]): Result = {
    global.ask { () =>
      val files = paths.map(x => new File(x))
      preRunner.run(preRunJobs, files)
      metricRunner.runFiles(metrics, files, projectContext)
    }
  }

  /**
    * Analyse complete project
    * @return result
    */
  def analyse(): Result  = {
    global.ask { () =>
      preRunner.run(preRunJobs, projectFiles)
      metricRunner.runProject(metrics, projectFiles, projectContext)
    }
  }
}

