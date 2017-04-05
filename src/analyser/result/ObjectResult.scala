package analyser.result

import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
class ObjectResult (position : RangePosition, name : String, metricName : String, value : Double) extends MetricResult(position, metricName, value)  {
  def getName : String = name
  override def toString: String = position + " Object(" + name + "): " + metricName + " = " + value
}
