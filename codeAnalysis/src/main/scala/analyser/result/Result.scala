package main.scala.analyser.result


import scala.reflect.internal.util.RangePosition

/**
  * Created by Erik on 5-4-2017.
  */
abstract class Result(val position: RangePosition){

  val file: String = position.pos.source.file.toString()

  def flatten() : List[MetricResult]
}


