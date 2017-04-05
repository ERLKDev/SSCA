package analyser.result

import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
class MetricResult(position : RangePosition, metricName : String, value : Double) extends Result {

  def getMetricName: String = metricName
  def getFile: String = position.pos.source.file.toString()
  def getPosition: RangePosition = position
  def getValue: Double = value

  override def toString: String = position + " " + metricName + ": " + value
}
