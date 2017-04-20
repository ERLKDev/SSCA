package main.scala.analyser.result



import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
class MetricResult(position : RangePosition, val name: String, val metricName : String, val value : Double) extends Result(position) {

  override def toString: String = position + " " + name + ": " + metricName + "= " + value

  override def flatten(): List[MetricResult] = List(this)

  override def toCsv: List[String] = List(value.toString)

  override def toCsvFunctions: List[String] = toCsv

  override def toCsvObject: List[String] = toCsv

  override def toCsvObjectSum: List[String] = toCsv

  override def toCsvObjectAvr: List[String] = toCsv

}