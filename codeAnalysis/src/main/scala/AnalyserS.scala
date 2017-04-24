import java.io.File

import analyser.PreRunner
import analyser.result.ResultUnit
import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.MetricRunner
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.Metric
import main.scala.analyser.prerun.PreRunJob
import main.scala.analyser.util.{ProjectUtil, ResultUtil}

import scala.reflect.internal.util.{Position, SourceFile}
import scala.tools.nsc.{Settings, util}
import scala.tools.nsc.interactive.{Global, Response}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.reporters.ConsoleReporter

/**
  * Created by Erik on 23-4-2017.
  */
class AnalyserS(projectPath: String, threads: Int) extends ProjectUtil with ResultUtil{
  private var projectFiles: List[File] = _
  private var results: List[ResultUnit] = List()
  private val compilerList: List[Compiler] =  List.fill(threads)(new Compiler)

  /* Always refresh the context*/
  refresh()


  /**
    * Refreshes the context
    */
  def refresh(): Unit = {
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
            compilerList(i).treeFromFile(x)
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


  class Compiler {
    lazy val global : Global = {

      val settings = new Settings
      settings.usejavacp.value = true

      val global = new Global(settings, new ConsoleReporter(settings) {
        override def printMessage(pos: Position, msg: String): Unit = {
        }
      })
      global.ask { () =>
        new global.Run
      }
      global
    }

    /**
      * Function to get the ast tree from a file
      *
      * @param file the source file
      * @return ast tree
      */
    def treeFromFile(file: SourceFile): global.Tree = {
      val response = new Response[global.Tree]

      global.ask(() => global.askLoadedTyped(file, true, response))

      response.get match {
        case Left(tree) => tree
        case Right(ex) => null
      }
    }

    /**
      * Function to get the ast tree from a file
      *
      * @param path the file path
      * @return ast tree
      */
    def treeFromFile(path: String): global.Tree = {
      val code = AbstractFile.getFile(path)
      val bfs = new util.BatchSourceFile(code, code.toCharArray)
      treeFromFile(bfs)
    }

    /**
      * Function to get the ast tree from a file
      *
      * @param file the file object
      * @return ast tree
      */
    def treeFromFile(file: File): global.Tree = {
      if (!file.exists())
        return null

      val code = AbstractFile.getFile(file)
      val bfs = new util.BatchSourceFile(code, code.toCharArray)
      treeFromFile(bfs)
    }
  }
}
