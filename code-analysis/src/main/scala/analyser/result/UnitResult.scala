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
}
