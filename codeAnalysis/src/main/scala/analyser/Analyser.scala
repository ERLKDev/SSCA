package main.scala.analyser

import java.io.File

import analyser.PreRunner
import analyser.result.ResultUnit
import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.Metric
import main.scala.analyser.prerun.PreRunJob
import main.scala.analyser.util.{ProjectUtil, ResultUtil}

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(projectPath: String, metrics : List[Metric]) extends CompilerProvider with ProjectUtil with ResultUtil{
  private val preRunner = new PreRunner
  private val metricRunner = new MetricRunner

  private var projectFiles: List[File] = _
  private var projectContext: ProjectContext = _
  private var results: List[ResultUnit] = _

  private val preRunJobs: List[PreRunJob] = metrics.filter(x => x.isInstanceOf[PreRunJob]).asInstanceOf[List[PreRunJob]]

  /* Always refresh the context*/
  refresh()


  /**
    * Refreshes the context
    */
  def refresh(): Unit = {
    projectFiles = getProjectFiles(projectPath).toList
    projectContext = new ProjectContext(projectFiles)
    results = List()

    /* Init main.scala.metrics*/
    metrics.foreach(f => f.init(projectContext))
  }

  /**
    * Analyse single file
    * @param path
    * @return result
    */
  def analyse(path: String): List[ResultUnit] = {
    global.ask { () =>
      val file = new File(path)
      preRunner.run(preRunJobs, List(file))
      results = List(metricRunner.run(metrics, file, projectContext))
    }
    results
  }

  /**
    * Analyse single file
    * @param paths
    * @return result
    */
  def analyse(paths: List[String]): List[ResultUnit] = {
    global.ask { () =>
      val files = paths.map(x => new File(x))
      preRunner.run(preRunJobs, files)
      results = addResults(results, metricRunner.runFiles(metrics, files, projectContext))
    }
    removeOldResults(results, projectFiles)
  }

  /**
    * Analyse complete project
    * @return result
    */
  def analyse(): List[ResultUnit]  = {
    global.ask { () =>
      preRunner.run(preRunJobs, projectFiles)
      results = addResults(results, metricRunner.runProject(metrics, projectFiles, projectContext))
    }
    removeOldResults(results, projectFiles)
  }
}

