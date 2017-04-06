package analyser.result

import analyser.result.UnitType.UnitType

/**
  * Created by Erik on 5-4-2017.
  */
trait Result {
  def flatten() : List[MetricResult]


  def getObjects : List[UnitResult] = get(UnitType.Object)

  def getFiles : List[UnitResult] = get(UnitType.File)

  def getFunctions : List[UnitResult] = get(UnitType.Function)


  private def get(uType: UnitType): List[UnitResult] = this match {
    case UnitResult(_, `uType`, _, _) =>
      List(this.asInstanceOf[UnitResult])
    case x: UnitResult =>
      x.getResults.foldLeft(List[UnitResult]())((a, b) => a ::: b.get(uType))
    case _ =>
      List[UnitResult]()
  }
}


