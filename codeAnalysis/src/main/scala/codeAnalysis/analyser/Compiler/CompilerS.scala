package codeAnalysis.analyser.Compiler

import java.io.File

import codeAnalysis.analyser.AST.AST
import main.scala.analyser.util.TreeSyntaxUtil

import scala.reflect.internal.util.{BatchSourceFile, Position, SourceFile}
import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.{Global, Response}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.reporters.ConsoleReporter

/**
  * Created by erikl on 4/24/2017.
  */
class CompilerS {
  lazy val global: Global = {

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

  val treeSyntaxUtil: TreeSyntaxUtil = new TreeSyntaxUtil(this)

  /**
    * Function to get the ast tree from a file
    *
    * @param file the source file
    * @return ast tree
    */
  def treeFromFile(file: SourceFile): AST = {
    val response = new Response[global.Tree]

    global.ask(() => global.askLoadedTyped(file, true, response))

    response.get match {
      case Left(tree) => {
        treeSyntaxUtil.parseTree(tree.asInstanceOf[treeSyntaxUtil.compiler.global.Tree])
      }
      case Right(ex) =>
        null
    }
  }

  /**
    * Function to get the ast tree from a file
    *
    * @param path the file path
    * @return ast tree
    */
  def treeFromFile(path: String): AST = {
    treeFromFile(new File(path))
  }

  /**
    * Function to get the ast tree from a file
    *
    * @param file the file object
    * @return ast tree
    */
  def treeFromFile(file: File): AST = {
    if (!file.exists())
      return null

    try {
      val code = AbstractFile.getFile(file)
      val bfs = new BatchSourceFile(code, code.toCharArray)
      treeFromFile(bfs)
    }catch {
      case _ =>
        null
    }

  }
}