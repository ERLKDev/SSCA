package main.scala.analyser.util

import analyser.result.{FunctionResult, ObjectResult, Result}

import scala.reflect.internal.util.RangePosition
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.util

/**
  * Created by ErikL on 4/7/2017.
  */
trait ResultUtil {

  def getObjects(results: List[Result]): List[ObjectResult] = {
    def recursive(resultList: List[Result]): List[ObjectResult] = {
      resultList.foldLeft(List[ObjectResult]())((a, b) => b match {
        case x: ObjectResult =>
          a ::: List(x) ::: recursive(x.results.toList)
        case _ =>
          a
      })
    }

    recursive(results)
  }

  def getFunctions(results: List[Result]): List[FunctionResult] = {
    def recursive(resultList: List[Result]): List[FunctionResult] = {
      resultList.foldLeft(List[FunctionResult]())((a, b) => b match {
        case x: FunctionResult =>
          a ::: List(x) ::: recursive(x.results.toList)
        case _ =>
          a
      })
    }

    recursive(results)
  }


  def fillCsvLine(line: List[String], size: Int) : List[String] = {
    if (size == 0 || line.length >= size)
      return line

    fillCsvLine(line, size - 1) ::: List("0")
  }

  def createPosition(path: String, startLine: Int, stopLine: Int): RangePosition = {
    def getOffsetFromLine(code: String, line: Int, incLast: Boolean): Int = {
      val codeLines = code.split("\n").toList
      codeLines.slice(0, line).mkString("\n").length + 1 + (if (incLast) codeLines(line).length else 0)
    }

    val absFile = AbstractFile.getFile(path)
    val bfs = new util.BatchSourceFile(absFile, absFile.toCharArray)

    val code = bfs.content.mkString
    val startOffset = getOffsetFromLine(code, startLine - 1, false)
    val stopOffset = getOffsetFromLine(code, stopLine, true)

    new RangePosition(bfs, startOffset, startOffset, stopOffset)
  }
}
