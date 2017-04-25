package codeAnalysis.analyser.result

import main.scala.analyser.util.ResultUtil

import scala.collection.mutable.ListBuffer
import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
abstract class ResultUnit(position: RangePosition) extends Result(position) with ResultUtil {
  val results: ListBuffer[Result] = ListBuffer.empty[Result]

  def addResult(result: Result): Unit = {
    this.results += result
  }

  def addResult(results: List[Result]): Unit = {
    this.results ++= results
  }

  def includes(startLine: Int, stopLine: Int) : Boolean = {
    position.includes(createPosition(position.source.path, startLine, startLine))
  }
}
