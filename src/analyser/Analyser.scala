package analyser

import java.io.File

import analyser.Compiler.CompilerProvider
import analyser.context.ProjectContext
import analyser.metric.Metric
import analyser.result.{MetricResult, Result}
import analyser.util.ProjectUtil
import metrics.{Complex, Loc}

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(projectPath: String, metrics : List[Metric]) extends CompilerProvider with ProjectUtil{
  import global._
  val projectFiles: Array[File] = getProjectFiles(projectPath)
  val projectTree: Array[global.Tree] = getProjectTree(projectPath)
  val metricRunner = new MetricRunner
  val projectContext: ProjectContext = PreRunner.run(projectTree.asInstanceOf[Array[PreRunner.global.Tree]])

  def getProjectTree: Array[Tree] = projectTree

  def analyse(path: String): Result = {
    metricRunner.run(metrics, Array(treeFromFile(path).asInstanceOf[metricRunner.global.Tree]), projectContext)
  }

  def analyse(): Result  = {
    metricRunner.run(metrics, projectTree.asInstanceOf[Array[metricRunner.global.Tree]], projectContext)
  }
}

