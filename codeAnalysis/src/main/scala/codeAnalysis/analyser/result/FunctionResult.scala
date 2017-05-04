package codeAnalysis.analyser.result

import main.scala.analyser.result.MetricResult
import main.scala.analyser.util.ResultUtil

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class FunctionResult(position : RangePosition, val name : String) extends ResultUnit(position) with ResultUtil{

  def isFunctionByName(name: String): Boolean = {
    name == this.name
  }

  override def toString: String = "\n" + position + " " + name + "$Function" + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())

  override def toCsvFunction: List[String] = {
    val metrics = results.filter(_.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
    .sortWith(_.metricName < _.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvFunction)
    val functions = getFunctions(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvFunction)

    metrics.mkString(",") :: functions
  }

  override def toCsvObject: List[String] = {
    null
  }

  override def toCsvObjectSum(size: Int): List[String] = {
    val metrics = results.filter(_.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith(_.metricName < _.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum(size))
    val functions = getFunctions(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum(size))

    metrics.mkString(",") :: functions
  }

  override def toCsvObjectAvr(size: Int): List[String] = {
    val metrics = results.filter(_.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith(_.metricName < _.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr(size))
    val functions = getFunctions(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr(size))

    metrics.mkString(",") :: functions
  }
}
