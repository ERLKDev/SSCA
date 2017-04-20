package analyser.result

import main.scala.analyser.result.MetricResult

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class FileResult(position: RangePosition, val name : String) extends ResultUnit(position){

  override def toString: String = "\n" + position + " " + name + "$File" + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())

  override def toCsv: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsv.map(x => name + "$file" + x))

  override def toCsvFunctions: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsvFunctions.map(x => name + "$file" + x))

  override def toCsvObject: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsvObject.map(x => name + "$file" + x))

  override def toCsvObjectSum: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectSum.map(x => name + "$file" + x))

  override def toCsvObjectAvr: List[String] = results.foldLeft(List[String]())((a, b) => a ::: b.toCsvObjectAvr.map(x => name + "$file" + x))

}
