package main.scala.analyser.result

/**
  * Created by ErikL on 4/6/2017.
  */
class ResultList(list: List[Result]) extends Result{
  def this() = this(List[Result]())

  def getList: List[Result] = list

  def add(result: Result): ResultList = result match {
    case x: ResultList =>
      new ResultList(this.list ::: x.getList)
    case _ =>
      new ResultList(this.list ::: List(result))
  }

  def toList: List[Result] = {
    def flat(list: List[Result]): List[Result] = list match {
      case Nil =>
        List[Result]()
      case (x: ResultList) :: tail =>
        flat(x.getList) ::: flat(tail)
      case x :: tail =>
        x :: flat(tail)
    }
    flat(list)
  }

  override def flatten(): List[MetricResult] = {
    def flat(results: List[Result]): List[MetricResult] = results match{
      case Nil =>
        List[MetricResult]()

      case x::tail =>
        x.flatten() ::: flat(tail)
    }

    flat(list)
  }

  override def toCSV: String = ""
}
