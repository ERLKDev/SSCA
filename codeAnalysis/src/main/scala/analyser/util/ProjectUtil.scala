package main.scala.analyser.util

import java.io.File

import main.scala.analyser.Compiler.CompilerProvider
/**
  * Created by Erik on 5-4-2017.
  */
trait ProjectUtil extends CompilerProvider {
  import global._

  def getProjectFiles(projectPath: String): Array[File] = {
    def listFiles(f: File): Array[File] = {
      val these = f.listFiles
      val scalaFiles = these.filter(f => """.*\.scala$""".r.findFirstIn(f.getName).isDefined)
      scalaFiles ++ these.filter(_.isDirectory).flatMap(listFiles)
    }

    listFiles(new File(projectPath))
  }
}
