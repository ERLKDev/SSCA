package main.scala.analyser.util

import java.io.File

import codeAnalysis.analyser.result._
import main.scala.Utils.SourceCodeUtil

import scala.reflect.internal.util.RangePosition
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.util

/**
  * Created by ErikL on 4/7/2017.
  */
trait ResultUtil extends SourceCodeUtil{

  /**
    * Fills the csv line with tailing zeros if the line is of a shorter length
    * @param line
    * @param size
    * @return
    */
  def fillCsvLine(line: List[String], size: Int) : List[String] = {
    if (size == 0 || line.length >= size)
      return line

    fillCsvLine(line, size - 1) ::: List("0")
  }


  def offsetToLine(code: String, offset: Int): Int = {
    val (first, _) = code.splitAt(offset)
    stringToLines(first).length - 1
  }

  /**
    * Creates a position
    *
    * @param path the file path
    * @param startLine the start line of the node
    * @param stopLine the stop line of the node
    * @return
    */
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
