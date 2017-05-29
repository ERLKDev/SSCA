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

  def toCSV(headerSize: Int): String = {
    val metricString = metrics.sortWith(_.metricName < _.metricName).map(_.toCsv)

    position.source.path + "|" + name + "," +  fillCsvLine(metricString, headerSize).mkString(",")
  }

  def functionPath: String = {
    position.source.path + "|" + name
  }
}
