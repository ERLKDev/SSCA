package main.scala.Utils

import java.io.File

import scala.io.Source

/**
  * Created by Erik on 5-4-2017.
  */
trait SourceCodeUtil {

  /**
    * Function that converts a string list to a string (separated by newlines)
    *
    * @param code the code list
    * @return
    */
  def linesToString(code: List[String]) : String = {
    code.foldLeft("")((a, b) => a + b + "\n")
  }


  /**
    * Function that converts a string to a string list
    *
    * @param code the code string
    * @return
    */
  def stringToLines(code: String) : List[String] = {
    code.toString.split("\n").toList
  }


  /**
    * Function that removes withe lines from the code
    *
    * @param code the code list
    * @return
    */
  def removeWhiteLines(code: List[String]): List[String] = {
    code.filter(s => ("""^\s*$""".r findFirstIn s).isEmpty)
  }

  def removeRedundantMultiLineComments(code: List[String]): List[String] = {
    stringToLines("""([ \t]*((\/\*)|(\*\/)|(\*))[ \t]*)""".r.replaceAllIn(linesToString(code), ""))
  }


  /**
    * Function that removes comments from the code
    * (only lines consisting completely out of comments)
    *
    * @param code the code list
    * @return
    */
  def removeComments(code: List[String]): List[String] = {
    val codeWithoutC = stringToLines("""((\/\*([\s\S]*?)\*\/)|\/\/(.*))""".r.replaceAllIn(linesToString(code), ""))
    removeWhiteLines(codeWithoutC)
  }


  def getComments(code: List[String]): List[String] = {
    val codeWithC = """.*((\/\*([\s\S]*?)\*\/)|\/\/(.*)).*""".r.findAllIn(linesToString(code)).mkString("\n")
    val codeWithNC = stringToLines("""([ \t]*((\/\*)|(\*\/)|(\*))[ \t]*)""".r.replaceAllIn(codeWithC, ""))
    removeWhiteLines(codeWithNC)
  }

  /**
    * Function that gets the list of files where a certain string in occurs
    * @param files all the files to search in
    * @param name the search string
    * @return
    */
  def getFilesOccurrence(files: List[File], name: String): List[File] = {
    files.foldLeft(List[File]()){
      (a, b) =>
        val file = Source.fromFile(b)
        val result = name.r findFirstMatchIn file.mkString
        file.close()
        if (result.nonEmpty)
          b :: a
        else
          a
    }
  }
}
