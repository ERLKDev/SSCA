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
    val codeWithC = """.*((\/\*([\s\S]*?)\*\/)|\/\/(.*)).*""".r.findAllIn(linesToString(code)).mkString
    val codeWithNC = stringToLines("""([ \t]*((\/\*)|(\*\/)|(\*))[ \t]*)""".r.replaceAllIn(codeWithC, ""))
    removeWhiteLines(codeWithNC)
  }

  /**
    * Function that gets the list of files where a certain string in occurs
    * @param files all the files to search in
    * @param names the search string
    * @return
    */
  def getFilesOccurrence(files: List[File], names: List[String]): List[File] = {
    val search = names.map(x => "(" + x + ")").mkString("|")


    val matches = files.foldLeft(Map[String, File]()){
      (a, b) =>
        val file = Source.fromFile(b)
        val result = search.r findAllMatchIn file.mkString
        file.close()
        a ++ result.foldLeft(List[String]())((c, d) => c ::: d.subgroups.filter(x => x != null)).map(x => x -> b)

    }
    println(matches)
    List()
/*    val result = names.foldLeft(Map[String, List[String]]()){
      (a, b) =>
        a + (b -> matches.filter(x => x.subgroups.contains(b)).map(x => x.)
    }*/

  }
}
