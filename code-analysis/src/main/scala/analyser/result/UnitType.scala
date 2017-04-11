package main.scala.analyser.result

/**
  * Created by ErikL on 4/6/2017.
  */
object UnitType extends Enumeration {
  type UnitType = Value
  val Object, Function, File, Project = Value


  override def toString = s"$Object, $Function, $Project)"
}