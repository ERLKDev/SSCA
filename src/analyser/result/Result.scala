package analyser.result

import analyser.result.UnitType.UnitType
import analyser.util.ResultUtil

import scala.reflect.internal.util.{Position, RangePosition}

/**
  * Created by Erik on 5-4-2017.
  */
trait Result extends ResultUtil{

  def flatten() : List[MetricResult]


  def getObjects : List[UnitResult] = getUnit(UnitType.Object)

  def getFiles : List[UnitResult] = getUnit(UnitType.File)

  def getFunctions : List[UnitResult] = getUnit(UnitType.Function)


  private def getUnit(uType: UnitType): List[UnitResult] = this match {
    case UnitResult(_, `uType`, _, _) =>
      List(this.asInstanceOf[UnitResult])
    case x: UnitResult =>
      x.getResults.foldLeft(List[UnitResult]())((a, b) => a ::: b.getUnit(uType))
    case _ =>
      List[UnitResult]()
  }

  def getFile(path: String) : UnitResult = {
    def findFile(files: List[UnitResult]) : UnitResult = files match {
      case Nil =>
        null
      case x::tail =>
        if (x.unitType == UnitType.File && x.position.source.path.equals(path)) {
          x
        }else {
          findFile(tail)
        }
    }
    findFile(this.getFiles)
  }

  def getObjects(path: String, startLine: Int, stopLine: Int) : List[UnitResult] = {
    def findObjects(pos: Position, objects: List[UnitResult]) : List[UnitResult] = objects match {
      case Nil =>
        List[UnitResult]()
      case x::tail =>
        if (x.unitType == UnitType.Object && x.position.includes(pos)) {
          x :: findObjects(pos, tail)
        }else{
          findObjects(pos, tail)
        }
    }

    val file = getFile(path)

    findObjects(createPosition(path, startLine, startLine), file.getObjects)
  }


  def getFunctions(path: String, startLine: Int, stopLine: Int) : List[UnitResult] = {
    def findFunction(pos: Position, objects: List[UnitResult]) : List[UnitResult] = objects match {
      case Nil =>
        List[UnitResult]()
      case x::tail =>
        if (x.unitType == UnitType.Object && x.position.includes(pos)) {
          x :: findFunction(pos, tail)
        }else{
          findFunction(pos, tail)
        }
    }

    val file = getFile(path)
    findFunction(createPosition(path, startLine, startLine), file.getFunctions)
  }

  def includes(startLine: Int, stopLine: Int) : Boolean = this match {
    case UnitResult(_, UnitType.Project, _, _) =>
      true
    case x: UnitResult =>
      x.position.includes(createPosition(x.position.source.path, startLine, startLine))
    case _ =>
      false
  }
}


