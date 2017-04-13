package main.scala.analyser.util

import scala.reflect.internal.util.RangePosition
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.util

/**
  * Created by ErikL on 4/7/2017.
  */
trait ResultUtil {

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
