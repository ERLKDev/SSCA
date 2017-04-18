package main.scala.analyser.result

import main.scala.analyser.result.UnitType.UnitType

import scala.reflect.internal.util.RangePosition

/**
  * Created by ErikL on 4/6/2017.
  */
case class UnitResult(position : RangePosition, unitType: UnitType, name : String, var results: List[Result]) extends Result {

  results = results.foldLeft(List[Result]()){
    (a, b) => a ::: (b match {
      case b: ResultList =>
        b.toList
      case _ =>
        List(b)
    })
  }

  def getName : String = name
  def getResults: List[Result] = results
  def getUnitType: UnitType = unitType


  def flatten(): List[MetricResult] = {
    def flat(results: List[Result]): List[MetricResult] = results match{
      case Nil =>
        List[MetricResult]()

      case x::tail =>
        x.flatten() ::: flat(tail)
    }
    flat(results)
  }

  override def toString: String = results match {
    case Nil =>
      ""
    case _ =>
      unitType + " " + name + ": {\n" + results.foldLeft("")((a, b) => a + b + "\n") + "}"
  }


  def toCSV : String= {
    val averageResults = results.filter{
      case _: MetricResult =>
        true
      case _ =>
        false
    }

    averageResults.foldLeft(position.source.path + "$" + name)((a, b) => a + ", " + b.toCSV)
  }
}

/*object UnitResult {
  def averageFunctions(objectUnit: UnitResult) : List[MetricResult] = {
    val filteredResults = objectUnit.getResults.foldLeft(List[UnitResult]()){
      (a, b) =>
        b match {
          case x: UnitResult =>
            if (x.getUnitType == UnitType.Function)
              a ::: List(x)
            else
              a
          case _ =>
            a
        }
    }.filter(x => x.getResults.exists {
      case _: MetricResult =>
        true
      case _ =>
        false
    })

    filteredResults.foldLeft(List[MetricResult]()) ((a, b) => combine(objectUnit, a, b))
      .map(x => MetricResult(x.position, x.resultType, x.name, x.metricName, x.getValue / filteredResults.length))
  }

  private def combine(objectUnit: UnitResult, metricResults: List[MetricResult], unitResult: UnitResult): List[MetricResult] = {
    unitResult.getResults.foldLeft(List[MetricResult]()) {
      (a, b) =>
        b match {
          case x: MetricResult =>
            metricResults.find(y => y.getMetricName == x.metricName) match {
              case Some(metric) =>
                a ::: List(MetricResult(objectUnit.position, objectUnit.unitType, objectUnit.name, x.metricName, metric.getValue + x.getValue))
              case _ =>
                a ::: List(MetricResult(objectUnit.position, objectUnit.unitType, objectUnit.name, x.metricName, x.getValue))
            }
          case _ =>
            a
        }
    }
  }
}*/
