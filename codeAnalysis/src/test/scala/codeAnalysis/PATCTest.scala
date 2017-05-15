package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import codeAnalysis.metrics.PATC
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 5/15/2017.
  */
class PATCTest extends UnitSpec{
  var metrics: List[Metric] = List(new PATC)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFilePATC.scala")
  var class1: ObjectResult = result.getClassByName("TestFilePATC").get

  def getMethodMetric(name: String, metric: String): Int = {
    class1.getFunctionByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Empty function") {
    val method = "test1"
    //val patc = getMethodMetric(method, "PATC")
    val patcMatch = getMethodMetric(method, "PATCmatch")

    //assert(patc == 0)
    assert(patcMatch == 0)
  }

  test("Match with 3 constructor function") {
    val method = "test2"
    //val patc = getMethodMetric(method, "PATC")
    val patcMatch = getMethodMetric(method, "PATCmatch")

    //assert(patc == 0)
    assert(patcMatch == 3)
  }
}
