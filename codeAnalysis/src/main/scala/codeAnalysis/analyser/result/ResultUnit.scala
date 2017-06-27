package codeAnalysis.analyser.result

import main.scala.analyser.result.MetricResult
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

  def includesPatch(patch: List[Int]) : Boolean = {
    val code = position.source.content.array.mkString
    val start = offsetToLine(code, position.start)
    val stop = offsetToLine(code, position.end)
    val lines = List.range(start, stop)
    lines.intersect(patch).nonEmpty
  }


  def includes(startLine: Int, stopLine: Int) : Boolean = {
    val code = position.source.content.array.mkString
    val start = offsetToLine(code, position.start)
    val stop = offsetToLine(code, position.end)
    start <= startLine && stopLine <= stop
  }

  def overlaps(startLine: Int, stopLine: Int) : Boolean = {
    val code = position.source.content.array.mkString
    val start = offsetToLine(code, position.start)
    val stop = offsetToLine(code, position.end)
    startLine <= stop && stopLine >= start
  }

  def childIncludes(startLine: Int, stopLine: Int) : Boolean = false

  def metrics: List[MetricResult] = {
    results.toList.filter{
      case _: MetricResult => true
      case _ => false
    }.map(_.asInstanceOf[MetricResult])
  }

  def functions: List[FunctionResult] = {
    results.toList.filter{
      case _: FunctionResult => true
      case _ => false
    }.map(_.asInstanceOf[FunctionResult])
  }

  def objects: List[ObjectResult] = {
    results.toList.filter{
      case _: ObjectResult => true
      case _ => false
    }.map(_.asInstanceOf[ObjectResult])
  }

  def nestedFunctions: List[FunctionResult] = {
      functions ::: functions.foldLeft(List[FunctionResult]())((a, b) => a ::: b.nestedFunctions)
  }

  def nestedObjects: List[ObjectResult] = {
      objects ::: (functions ::: objects).foldLeft(List[ObjectResult]())((a, b) => a ::: b.nestedObjects)
  }


  def getClassByName(name: String): Option[ObjectResult] = {
    results.find{
      case obj: ObjectResult =>
        obj.name == name
      case _ =>
        false
    }.asInstanceOf[Option[ObjectResult]]
  }

  def getFunctionByName(name: String): Option[FunctionResult] = {
    results.find{
      case obj: FunctionResult =>
        obj.name == name
      case _ =>
        false
    }.asInstanceOf[Option[FunctionResult]]
  }

  def getMetricByName(name: String): Option[MetricResult] = {
    results.find{
      case obj: MetricResult =>
        obj.metricName == name
      case _ =>
        false
    }.asInstanceOf[Option[MetricResult]]
  }
}
