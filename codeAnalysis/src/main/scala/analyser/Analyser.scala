package main.scala.analyser

import java.io.File

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.Metric
import main.scala.analyser.result.{MetricResult, Result}
import main.scala.analyser.util.ProjectUtil
import main.scala.metrics.{Complex, Loc}

/**
  * Created by Erik on 5-4-2017.
  */
class Analyser(projectPath: String, metrics : List[Metric]) extends CompilerProvider with ProjectUtil{
  import global._
  val projectFiles: List[File] = getProjectFiles(projectPath).toList
  //val projectTree: Array[global.Tree] = getProjectTree(projectPath)
  val metricRunner = new MetricRunner

  //val projectContext: ProjectContext = PreRunner.run(projectFiles)


  //def getProjectTree: Array[Tree] = projectTree

  def analyse(path: String): Result = {
    global.ask { () =>
      val a = new File(path)
      metricRunner.run(metrics, List(a), new ProjectContext(projectFiles))
    }
  }

  def analyse(): Result  = {
    global.ask { () =>
      metricRunner.run(metrics, projectFiles, new ProjectContext(projectFiles))
    }
  }
}

