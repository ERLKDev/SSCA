package main.scala.Utils

import java.io.File

/**
  * Created by Erik on 5-4-2017.
  */
trait SourceCodeUtil {
  def linesToString(code: List[String]) : String = {
    code.foldLeft("")((a, b) => a + b + "\n")
  }

  def stringToLines(code: String) : List[String] = {
    code.toString.split("\n").toList
  }

  def removeWhiteLines(code: List[String]): List[String] = {
    val a = code.filter(s => ("""^\s*$""".r findFirstIn s).isEmpty)
    a
  }

  def removeComments(code: List[String]): List[String] = {
    val codeWMC = stringToLines("""\/\*([\s\S]*?)\*\/""".r.replaceAllIn(linesToString(code), ""))
    removeWhiteLines(codeWMC.filter(s =>("""^((\s)*\/\/.*)""".r findFirstIn s).isEmpty))
  }

  def getFilesOccurrence(files: List[File], search: String): List[File] = {
    files.filter(x => (search.r findFirstIn scala.io.Source.fromFile(x).mkString).nonEmpty)
  }
}
