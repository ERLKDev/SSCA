package analyser.Compiler

import scala.reflect.internal.util.{Position, SourceFile}
import scala.tools.nsc.{Settings, util}
import scala.tools.nsc.interactive.{Global, Response}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.reporters.ConsoleReporter

/**
  * Created by Erik on 5-4-2017.
  */
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
}

/**
  * Helper trait to define parse functions for the compiler
  */
trait CompilerHelper {
  val global: scala.tools.nsc.interactive.Global

  def treeFromFile(file: SourceFile): global.Tree = {
    val response = new Response[global.Tree]

    global.ask(() => global.askLoadedTyped(file, true, response))

    response.get match {
      case Left(tree) => tree
      case Right(ex) => null
    }
  }

  def treeFromFile(file: String): global.Tree = {
    val code = AbstractFile.getFile(file)
    val bfs = new util.BatchSourceFile(code, code.toCharArray)
    treeFromFile(bfs)
  }
}

object Compiler extends Compiler

trait CompilerProvider extends CompilerHelper{
  val global: Global = Compiler.global
}
