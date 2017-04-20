package analyser.result

import analyser.result.ObjectType.ObjectType
import main.scala.analyser.result.{MetricResult, Result}

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class ObjectResult(position : RangePosition, val name : String, val objectType: ObjectType) extends ResultUnit(position){

  override def toString: String = "\n" + position + " " + name + "$" + objectType + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())

  override def toCsv: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsv.map(x => "|" + name + "$" + objectType + x))

  override def toCsvFunctions: List[String] = {
    results.filter(x => x.isInstanceOf[ResultUnit]).toList.asInstanceOf[List[ResultUnit]]
      .foldLeft(List[String]())((a, b) => a ::: b.toCsvFunctions.map(x => "|" + name + "$" + objectType + x))
  }

  override def toCsvObject: List[String] = {
    val metrics = results.filter(x => x.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith((a, b) => a.metricName < b.metricName).foldLeft("|" + name + "$" + objectType)((a, b) => a + ", " + b.toCsvObject.mkString)

    val units = results.filter(x => x.isInstanceOf[ResultUnit]).toList.asInstanceOf[List[ResultUnit]]
      .foldLeft(List[String]())((a, b) => a ::: b.toCsvObject.map(x => "|" + name + "$" + objectType + x))

    metrics :: units
  }

  override def toCsvObjectSum: List[String] = {
    val metrics = results.filter(x => x.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith((a, b) => a.metricName < b.metricName).foldLeft("|" + name + "$" + objectType)((a, b) => a + ", " + b.toCsvObjectSum.mkString)

    val functionAverage = getFunctionsInObject.map(x => x.toCsvObjectSum).transpose.map(x => x.map(y => y.toDouble).sum).mkString(", ")

    val units = results.filter(x => x.isInstanceOf[ObjectResult]).toList.asInstanceOf[List[ObjectResult]]
      .foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum.map(x => "|" + name + "$" + objectType + x))

    (metrics + ", " + functionAverage) :: units
  }

  override def toCsvObjectAvr: List[String] = {
    val metrics = results.filter(x => x.isInstanceOf[MetricResult]).toList.asInstanceOf[List[MetricResult]]
      .sortWith((a, b) => a.metricName < b.metricName).foldLeft("|" + name + "$" + objectType)((a, b) => a + ", " + b.toCsvObjectAvr.mkString)

    val functionAverage = getFunctionsInObject.map(x => x.toCsvObjectAvr).transpose.map(x => x.map(y => y.toDouble).sum / getFunctionsInObject.length).mkString(", ")

    val units = results.filter(x => x.isInstanceOf[ObjectResult]).toList.asInstanceOf[List[ObjectResult]]
      .foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr.map(x => "|" + name + "$" + objectType + x))

    (metrics + ", " + functionAverage) :: units
  }


  private def getFunctionsInObject: List[FunctionResult] = {
    def recursive(resultList: List[Result]): List[FunctionResult] = {
      resultList.foldLeft(List[FunctionResult]())((a, b) => b match {
        case x: FunctionResult =>
          a ::: List(x) ::: recursive(x.results.toList)
        case _ =>
          a
      })
    }

    recursive(results.toList)
  }
}
