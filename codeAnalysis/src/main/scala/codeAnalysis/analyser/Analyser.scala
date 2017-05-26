package main.scala.analyser

import java.io.File

import codeAnalysis.analyser.Compiler.CompilerS
import codeAnalysis.analyser.result.ResultUnit
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.Metric
import main.scala.analyser.util.{ProjectUtil, ResultUtil}

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(metrics: List[Metric], projectPath: String, threads: Int) extends ProjectUtil with ResultUtil{

  private val comp = new CompilerS
  private var projectFiles: List[File] = List()
  private var projectContext: ProjectContext = _

  private val compilers: List[CompilerS] = List.fill(threads)(new CompilerS)

  /* Always refresh the context on init*/
  refresh()

  /**
    * Refreshes the context and project files
    */
  def refresh(): Unit = {
    projectFiles = getProjectFiles(projectPath).toList
    projectContext = new ProjectContext(comp, projectFiles, true, 50)
    metrics.foreach(_.init(projectContext))
  }


  /**
    * Start the code analysis
    *
    * @param paths the project files
    * @return
    */
  private def startAnalysis(paths: List[File]): List[ResultUnit] = {
    if (paths.isEmpty)
      return List()

    /* Divides the files into chunks */
    val chunks = paths.grouped(math.ceil(paths.length.toDouble / (if (threads < paths.length) threads else paths.length)).toInt).toList
    val result = chunks.zipWithIndex.par.map{
      case (x, i) =>
        val metricRunner = new MetricRunner(compilers(i), metrics, projectContext)

        metricRunner.runFiles(metrics, x)
    }.fold(List[ResultUnit]())((a, b) => a ::: b)

    result
  }

  /**
    * Function to analyse a single file
    * @param path the file path
    * @return
    */
  def analyse(path: String): ResultUnit = {
    startAnalysis(List(new File(path))).head
  }

  /**
    * Function to analyse a list of files
    *
     * @param paths the file paths
    * @return
    */
  def analyse(paths: List[String]): List[ResultUnit] = {
    startAnalysis(paths.map(x => new File(x)))
  }

  /**
    * Function to analyse the entire project
    *
    * @return
    */
  def analyse(): List[ResultUnit]  = {
    startAnalysis(projectFiles)
  }

  /**
    * Function to close the metric analysis
    */
  def close(): Unit = {
    compilers.foreach(x => x.global.askShutdown())
    //comp.global.askShutdown()
  }
}


