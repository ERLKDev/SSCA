package codeAnalysis.analyser.result

import main.scala.analyser.result.MetricResult
import main.scala.analyser.util.ResultUtil

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/20/2017.
  */
class FunctionResult(position : RangePosition, val name : String) extends ResultUnit(position) with ResultUtil{

  def isFunctionByName(name: String): Boolean = {
    name == this.name
  }

  def normalize(): List[Result] = {
    val functs = functions.foldLeft(List[Result]())((a, b) => a ::: b.normalize())
    val objs = objects.foldLeft(List[Result]())((a, b) => a ::: b.normalize())
    objs ::: functs
  }

  override def toString: String = "\n" + position + " " + name + "$Function" + "{\n" + results.map(x => "\t" + x).mkString("\n") + "\n}\n"

  override def flatten(): List[MetricResult] = results.foldLeft(List[MetricResult]())((a, b) => a ::: b.flatten())
}
