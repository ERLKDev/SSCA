package analyser

import java.io.File

import analyser.Compiler.CompilerProvider
import analyser.result.MetricResult
import analyser.util.ProjectUtil
import metrics.Loc

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(projectPath: String) extends CompilerProvider with ProjectUtil{
  import global._
  val projectFiles: Array[File] = getProjectFiles(projectPath)
  val projectTree: Array[global.Tree] = getProjectTree(projectPath)
  val metricRunner = new MetricRunner

  def getProjectTree: Array[Tree] = projectTree

  def analyse(path: String) : List[MetricResult] = {
    metricRunner.run(List(new Loc), Array(treeFromFile(path).asInstanceOf[metricRunner.global.Tree]), projectTree.asInstanceOf[Array[metricRunner.global.Tree]])
  }

  def analyse() : List[MetricResult] = {
    metricRunner.run(List(new Loc), projectTree.asInstanceOf[Array[metricRunner.global.Tree]], projectTree.asInstanceOf[Array[metricRunner.global.Tree]])
  }
}

