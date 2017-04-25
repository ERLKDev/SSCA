package codeAnalysis.analyser.result

import main.scala.analyser.result.MetricResult

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class FileResult(position: RangePosition, val name : String) extends ResultUnit(position){

  override def toString: String = "\n" + position + " " + name + "$File" + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())

  override def toCsvFunction: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsvFunction.map(x => x))

  override def toCsvObject: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsvObject.map(x => x))

  override def toCsvObjectSum(size: Int): List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum(size).map(x => x))

  override def toCsvObjectAvr(size: Int): List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr(size).map(x => x))

}
