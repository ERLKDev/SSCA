package main.scala.analyser

import java.io.File

import codeAnalysis.analyser.Compiler.CompilerS
import codeAnalysis.analyser.PreRunner
import codeAnalysis.analyser.result.ResultUnit
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.Metric
import main.scala.analyser.prerun.PreRunJob
import main.scala.analyser.util.{ProjectUtil, ResultUtil}

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(metrics: List[Metric], projectPath: String, threads: Int) extends ProjectUtil with ResultUtil{
  private var projectFiles: List[File] = List()

  private val comp = new CompilerS

  private var projectContext: ProjectContext = _
  private var results: List[ResultUnit] = List()

  private val preRunJobs: List[PreRunJob] = metrics.filter(x => x.isInstanceOf[PreRunJob]).asInstanceOf[List[PreRunJob]]

  /* Always refresh the context*/
  refresh()


  /**
    * Refreshes the context
    */
  def refresh(): Unit = {
    projectFiles = getProjectFiles(projectPath).toList
    projectContext = new ProjectContext(projectFiles)
    metrics.foreach(_.init(projectContext))
  }


  private def startAnalysis(paths: List[File]): List[ResultUnit] = {
    if (paths.isEmpty)
      return List()

    results = removeOldResults(results, projectFiles)

    val chunks = paths.grouped(math.ceil(paths.length.toDouble / (if (threads < paths.length) threads else paths.length)).toInt).toList
    val result = chunks.zipWithIndex.par.map{
      case (x, i) =>
        val preRunner = new PreRunner(comp)
        val metricRunner = new MetricRunner(comp, metrics)

        preRunner.run(preRunJobs, x)
        metricRunner.runFiles(metrics, x)
    }.fold(List[ResultUnit]())((a, b) => a ::: b)

    results = addResults(results, result)
    results
  }

  def analyse(path: String): ResultUnit = {
    startAnalysis(List(new File(path))).head
  }

  def analyse(paths: List[String]): List[ResultUnit] = {
    startAnalysis(paths.map(x => new File(x)))
  }

  def analyse(): List[ResultUnit]  = {
    startAnalysis(projectFiles)
  }

  def close(): Unit = {
    comp.global.askShutdown()
  }
}


