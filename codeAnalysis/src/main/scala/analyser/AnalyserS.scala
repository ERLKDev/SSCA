package analyser

import java.io.File

import analyser.Compiler.CompilerS
import analyser.result.ResultUnit
import main.scala.analyser.MetricRunner
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.util.{ProjectUtil, ResultUtil}

/**
  * Created by Erik on 23-4-2017.
  */
class AnalyserS(projectPath: String, threads: Int) extends ProjectUtil with ResultUtil{
  private var projectFiles: List[File] = _
  private val compilerList: List[CompilerS] =  List.fill(threads)(new CompilerS)

  private var projectContext: ProjectContext = _
  private var results: List[ResultUnit] = List()

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
    chunks.zipWithIndex.par.foreach{
      case(b, i) =>
        println("start one")
        b.foreach{
          x =>
            val preRunner = new PreRunner(compilerList(i))
            val metricRunner = new MetricRunner(compilerList(i))
            preRunner.run(null, paths)
            metricRunner.runFiles(null, paths, projectContext)

        }
    }
    List()
  }

  def analyse(paths: List[String]): List[ResultUnit] = {
    startAnalysis(paths.map(x => new File(x)))
  }

  def analyse(): List[ResultUnit]  = {
    startAnalysis(projectFiles)
  }
}
