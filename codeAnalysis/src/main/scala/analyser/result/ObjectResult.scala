package analyser.result

import analyser.result.ObjectType.ObjectType
import main.scala.analyser.result.MetricResult

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class ObjectResult(position : RangePosition, val name : String, val objectType: ObjectType) extends ResultUnit(position){

  override def toString: String = "\n" + position + " " + name + "$" + objectType + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())

  override def toCsvFunction: List[String] = {
    val metrics = results.filter(_.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith(_.metricName < _.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvFunction)

    val objects = getObjects(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvFunction)
    val functions = getFunctions(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvFunction)

    metrics.mkString(", ") :: objects ::: functions
  }

  override def toCsvObject: List[String] = {
    val metrics = results.filter(_.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith(_.metricName < _.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvObject)

    val objects = getObjects(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvObject)

    position.source.path + "|" + name + "%{" + objectType + "}, " + metrics.mkString(", ") :: objects
  }

  override def toCsvObjectSum(size: Int): List[String] = {
    val metrics = results.filter(_.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith(_.metricName < _.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum(size))

    val objects = getObjects(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum(size))
    val functions = getFunctions(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum(size))

    val funSum = functions.map(x => x.split(", ").toList).transpose.map(x => x.map(_.toDouble).sum.toString)

    position.source.path + "|" + name + "%{" + objectType + "}, " + fillCsvLine(metrics ::: funSum, size).mkString(", ") :: objects
  }

  override def toCsvObjectAvr(size: Int): List[String] = {
    val metrics = results.filter(_.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith(_.metricName < _.metricName).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr(size))

    val objects = getObjects(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr(size))
    val functions = getFunctions(results.toList).foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr(size))

    val funSum = functions.map(x => x.split(", ").toList).transpose.map(x => (x.map(_.toDouble).sum / functions.length).toString)

    position.source.path + "|" + name + "%{" + objectType + "}, " + (metrics ::: funSum).mkString(", ") :: objects
  }
}
