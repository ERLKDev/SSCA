package analyser.Compiler

import java.io.File

import scala.reflect.internal.util.{BatchSourceFile, Position, SourceFile}
import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.{Global, Response}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.reporters.ConsoleReporter

/**
  * Created by erikl on 4/24/2017.
  */
class CompilerS {
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
  def treeFromFile(file: SourceFile): TreeWrapper = {
    val response = new Response[global.Tree]

    global.ask(() => global.askLoadedTyped(file, true, response))

    response.get match {
      case Left(tree) => {
        val wrapper = new TreeWrapper(this)
        wrapper.wrap(tree.asInstanceOf[wrapper.compiler.global.Tree])
        wrapper
      }
      case Right(ex) => null
    }
  }

  /**
    * Function to get the ast tree from a file
    *
    * @param path the file path
    * @return ast tree
    */
  def treeFromFile(path: String): TreeWrapper = {
    val code = AbstractFile.getFile(path)
    val bfs = new BatchSourceFile(code, code.toCharArray)
    treeFromFile(bfs)
  }

  /**
    * Function to get the ast tree from a file
    *
    * @param file the file object
    * @return ast tree
    */
  def treeFromFile(file: File): TreeWrapper = {
    if (!file.exists())
      return null

    val code = AbstractFile.getFile(file)
    val bfs = new BatchSourceFile(code, code.toCharArray)
    treeFromFile(bfs)
  }
}
