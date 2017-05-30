package main.scala.analyser.util

import java.io.File
import codeAnalysis.analyser.AST._

/**
  * Created by Erik on 5-4-2017.
  */
trait ProjectUtil {

  /**
    * Gets a list of all the scala files in a project
    *
    * @param projectPath the path of the project
    * @return a list of all the scala files in the project
    */
  def getProjectFiles(projectPath: String): Array[File] = {
    def listFiles(f: File): Array[File] = {
      val these = f.listFiles
      val scalaFiles = these.filter(f => """.*\.scala$""".r.findFirstIn(f.getName).isDefined)
      scalaFiles ++ these.filter(_.isDirectory).flatMap(listFiles)
    }

    /* If testing: the test directory isn't filtered out*/
    if (System.getenv("testing") == "y")
      listFiles(new File(projectPath))
    else
      listFiles(new File(projectPath)).filter(f => """(\\test\\)|(\/test\/)""".r.findFirstIn(f.getPath).isEmpty)
  }

  /**
    * Function to get the original source code of a tree
    *
    * @param tree The tree
    * @return
    */
  def getOriginalSourceCode(tree : AST): List[String] = {
    if (tree.pos == null)
      return null
    tree.pos.source.content.array.subSequence(tree.pos.start, tree.pos.end).toString.split("\n").toList

  }
}
