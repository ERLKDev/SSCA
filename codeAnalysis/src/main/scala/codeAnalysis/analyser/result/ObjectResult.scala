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

  override def childIncludes(startLine: Int, stopLine: Int): Boolean = {
    def getChildObjects(resultUnit: ResultUnit): List[ResultUnit] = resultUnit match {
      case x: ObjectResult =>
        x :: (x.objects ::: x.functions).foldLeft(List[ResultUnit]())((a, b) => a ::: getChildObjects(b))
      case x: ResultUnit =>
        (x.objects ::: x.functions).foldLeft(List[ResultUnit]())((a, b) => a ::: getChildObjects(b))
      case _ =>
        List()
    }
    (objects ::: functions).foldLeft(List[ResultUnit]())((a, b) => a ::: getChildObjects(b))
      .exists(x => x.includes(startLine, stopLine) || x.childIncludes(startLine, stopLine))
  }

  def normalize(): ObjectResult = {
    val obj = new ObjectResult(position, name, objectType)
    obj.addResult(metrics ::: nestedFunctions)
    obj
  }

  def toCSV(headerSize: Int): String = {
    val norm = normalize()
    val metricString =
      norm.metrics.sortWith(_.metricName < _.metricName).map(_.toCsv) ::: avr(norm.functions).map(_.toCsv) ::: sum(norm.functions).map(_.toCsv) ::: max(norm.functions).map(_.toCsv)

    objectPath+ "," +  fillCsvLine(metricString, headerSize).mkString(",")
  }

  def objectPath: String = {
    name + "%{" + objectType + "}"
  }

  def avr(functions: List[FunctionResult]) : List[MetricResult] = {
    functions.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "functionAvr" + x.head.metricName.capitalize, x.map(_.value.toDouble).sum / x.length))
  }

  def sum(functions: List[FunctionResult]) : List[MetricResult] = {
    functions.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "functionSum" + x.head.metricName.capitalize, x.map(_.value.toDouble).sum))
  }

  def max(functions: List[FunctionResult]) : List[MetricResult] = {
    functions.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "functionSum" + x.head.metricName.capitalize, x.map(_.value.toDouble).max))
  }
}
