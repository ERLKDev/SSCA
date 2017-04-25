package codeAnalysis.analyser.result


import main.scala.analyser.result.MetricResult

import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
abstract class Result(val position: RangePosition){

  val file: String = position.pos.source.file.toString()

  def flatten() : List[MetricResult]

  def toCsvFunction: List[String]

  def toCsvObject: List[String]

  def toCsvObjectSum(size: Int): List[String]

  def toCsvObjectAvr(size: Int): List[String]
}


