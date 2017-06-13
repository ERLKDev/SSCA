package codeAnalysis.analyser.result

import main.scala.analyser.result.MetricResult

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class FileResult(position: RangePosition, val name : String) extends ResultUnit(position){

  override def toString: String = "\n" + position + " " + name + "$File" + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())

  def isFileByName(name: String): Boolean = {
    name == this.name
  }

  def isFileByPath(path: String): Boolean = {
    path == this.position.source.path
  }

  def toCSV(headerSize: Int): String = {
    val metricString =
      avrO(objects).map(_.toCsv) ::: sumO(objects).map(_.toCsv) ::: maxO(objects).map(_.toCsv) //:::
      //avrF(functions).map(_.toCsv) ::: sumF(functions).map(_.toCsv) ::: maxF(functions).map(_.toCsv)

     position.source.path + "|" + name + "%{file}," +  fillCsvLine(metricString, headerSize).mkString(",")
  }

  def filePath: String = {
    position.source.path + "|" + name + "%{file}"
  }

  def avrF(functions: List[FunctionResult]) : List[MetricResult] = {
    functions.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "functionAvr" + x.head.metricName.capitalize, x.map(_.value.toDouble).sum / x.length))
  }

  def avrO(objects: List[ObjectResult]) : List[MetricResult] = {
    objects.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "objectAvr" + x.head.metricName.capitalize, x.map(_.value.toDouble).sum / x.length))
  }

  def sumF(functions: List[FunctionResult]) : List[MetricResult] = {
    functions.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "functionSum" + x.head.metricName.capitalize, x.map(_.value.toDouble).sum))
  }

  def sumO(objects: List[ObjectResult]) : List[MetricResult] = {
    objects.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "objectSum" + x.head.metricName.capitalize, x.map(_.value.toDouble).sum))
  }

  def maxF(functions: List[FunctionResult]) : List[MetricResult] = {
    functions.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "functionMax" + x.head.metricName.capitalize, x.map(_.value.toDouble).max))
  }

  def maxO(objects: List[ObjectResult]) : List[MetricResult] = {
    objects.foldLeft(List[List[MetricResult]]())((a, b) => b.metrics.sortWith(_.metricName < _.metricName) :: a)
      .transpose.map(x => new MetricResult(position, name, "objectMax" + x.head.metricName.capitalize, x.map(_.value.toDouble).max))
  }
}
