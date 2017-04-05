package analyser.util

import analyser.result.MetricResult

/**
  * Created by Erik on 5-4-2017.
  */
trait PrintUtil {
  def printresults(results: List[MetricResult]) : Unit = {
    val sorted = results.sortWith((a, b) => a.getFile + a.getPosition.start.toString > b.getFile + b.getPosition.start.toString)
    sorted.foreach(x => println(x))
  }
}
