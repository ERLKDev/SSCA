package main.scala.analyser

import java.io.File

import analyser.Compiler.CompilerS
import analyser.PreRunner
import analyser.result.ResultUnit
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.Metric
import main.scala.analyser.prerun.PreRunJob
import main.scala.analyser.util.{ProjectUtil, ResultUtil}

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(metrics: List[Metric], projectPath: String, threads: Int) extends ProjectUtil with ResultUtil{
  private var projectFiles: List[File] = _
  private val compilerList: List[CompilerS] =  List.fill(threads)(new CompilerS)

  private var projectContext: ProjectContext = _
  private var results: List[ResultUnit] = List()

  private val preRunJobs: List[PreRunJob] = metrics.filter(x => x.isInstanceOf[PreRunJob]).asInstanceOf[List[PreRunJob]]

  /* Always refresh the context*/
  refresh()


  /**
    * Refreshes the context
    */
  def refresh(): Unit = {
    projectContext = new ProjectContext(projectFiles)
    projectFiles = getProjectFiles(projectPath).toList
  }


  private def startAnalysis(paths: List[File]): List[ResultUnit] = {
    if (paths.isEmpty)
      return List()

    val chunks = paths.grouped(math.ceil(paths.length.toDouble / (if (threads < paths.length) threads else paths.length)).toInt).toList
    chunks.zipWithIndex.par.map{
      case (x, i) =>
        println("start one")
        val preRunner = new PreRunner(compilerList(i))
        val metricRunner = new MetricRunner(compilerList(i))
        preRunner.run(preRunJobs, x)
        metricRunner.runFiles(metrics, x, projectContext)
    }.fold(List[ResultUnit]())((a, b) => a ::: b)
  }

  def analyse(paths: String): List[ResultUnit] = {
    startAnalysis(List(new File(paths)))
  }

  def analyse(paths: List[String]): List[ResultUnit] = {
    startAnalysis(paths.map(x => new File(x)))
  }

  def analyse(): List[ResultUnit]  = {
    startAnalysis(projectFiles)
  }
}


