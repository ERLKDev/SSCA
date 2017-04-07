package analyser.util

import java.io.File

import analyser.Compiler.CompilerProvider
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

  def getProjectTree(projectPath: String): Array[Tree] = {
    getProjectFiles(projectPath).foldLeft(Array[Tree]()){
      (a, f) =>
        val tree = treeFromFile(f.getAbsolutePath)
        if (tree == null)
          return a
        a :+ tree
    }
  }

}
