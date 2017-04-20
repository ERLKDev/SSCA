package analyser.result

import main.scala.analyser.result.MetricResult

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class FileResult(position: RangePosition, val name : String) extends ResultUnit(position){

  override def toString: String = "\n" + position + " " + name + "$File" + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = ???
}
