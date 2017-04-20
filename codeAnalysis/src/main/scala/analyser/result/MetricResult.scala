package main.scala.analyser.result



import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
class MetricResult(position : RangePosition, val name: String, val metricName : String, val value : Double) extends Result(position) {

  override def toString: String = metricName + "= " + value

  override def flatten(): List[MetricResult] = ???

}