package codeAnalysis.analyser.result

import codeAnalysis.analyser.result.ObjectType.ObjectType
import main.scala.analyser.result.MetricResult

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class ObjectResult(position : RangePosition, val name : String, val objectType: ObjectType) extends ResultUnit(position){

  def isObjectByName(name: String): Boolean = {
    name == this.name
  }

  override def toString: String = "\n" + position + " " + name + "$" + objectType + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())

  def normalize(): List[ObjectResult] = {
    val obj = new ObjectResult(position, name, objectType)
    obj.addResult(metrics ::: nestedFunctions)
    obj :: nestedObjects.foldLeft(List[ObjectResult]())((a, b) => a ::: b.normalize())
  }

  def toCSV(headerSize: Int): List[String] = {
    val norm = normalize()
    norm.foldLeft(List[String]()){
      (a, b) =>
        val g = b.metrics
        val metricString = b.metrics.sortWith(_.metricName < _.metricName).map(_.toCsv) ::: avr(b.functions).map(_.toCsv) ::: sum(b.functions).map(_.toCsv)

        b.position.source.path + "|" + b.name + "%{" + b.objectType + "}," +  fillCsvLine(metricString, headerSize).mkString(",") :: a
    }
  }

  def objectPath: String = {
    position.source.path + "|" + name + "%{" + objectType + "}"
  }

  def avr(functions: List[FunctionResult]) : List[MetricResult] = {
    functions.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "functionAvr" + x.head.metricName.capitalize, x.map(_.value.toDouble).sum / x.length))
  }

  def sum(functions: List[FunctionResult]) : List[MetricResult] = {
    functions.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "functionSum" + x.head.metricName.capitalize, x.map(_.value.toDouble).sum))
  }
}
