package main.scala.analyser.result



import main.scala.analyser.result.UnitType.UnitType

import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
case class MetricResult(position : RangePosition, resultType: UnitType, name: String, metricName : String, value : Double) extends Result {

  def getMetricName: String = metricName
  def getFile: String = position.pos.source.file.toString()
  def getPosition: RangePosition = position
  def getValue: Double = value
  def getResultType: UnitType = resultType
  def getName: String = name

  override def toString: String = position + " " + resultType + "(" + name + "): " + metricName + " = " + value

  override def flatten(): List[MetricResult] = List(this)
}
