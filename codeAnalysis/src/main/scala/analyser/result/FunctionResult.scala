package analyser.result

import main.scala.analyser.result.MetricResult

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class FunctionResult(position : RangePosition, val name : String) extends ResultUnit(position){

  override def toString: String = "\n" + position + " " + name + "$Function" + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())

  override def toCsv: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsv.map(x => "|" + name + "$function" + x))

  override def toCsvFunctions: List[String] = {
    val metrics = results.filter(x => x.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith((a, b) => a.metricName < b.metricName).foldLeft("|" + name + "$function")((a, b) => a + ", " + b.toCsvFunctions.mkString)

    val units = results.filter(x => x.isInstanceOf[ResultUnit]).toList.asInstanceOf[List[ResultUnit]]
      .foldLeft(List[String]())((a, b) => a ::: b.toCsvFunctions.map(x => "|" + name + "$function" + x))

    metrics :: units
  }

  override def toCsvObject: List[String] = {
    List()
  }

  override def toCsvObjectSum: List[String] = {
    results.filter(x => x.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith((a, b) => a.metricName < b.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum)
  }

  override def toCsvObjectAvr: List[String] = {
    results.filter(x => x.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith((a, b) => a.metricName < b.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr)
  }
}
