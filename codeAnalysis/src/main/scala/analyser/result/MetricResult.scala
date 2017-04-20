package main.scala.analyser.result



import analyser.result.Result

import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
class MetricResult(position : RangePosition, val name: String, val metricName : String, val value : Double) extends Result(position) {

  override def toString: String = position + " " + name + ": " + metricName + "= " + value

  override def flatten(): List[MetricResult] = List(this)

  override def toCsvFunction: List[String] = List(value.toString)

  override def toCsvObject: List[String] = toCsvFunction

  override def toCsvObjectSum(size: Int): List[String] = toCsvFunction

  override def toCsvObjectAvr(size: Int): List[String] = toCsvFunction

}