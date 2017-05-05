package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric
import main.scala.metrics.WMC

/**
  * Created by erikl on 5/4/2017.
  */
class WMCTest extends UnitSpec{
  var metrics: List[Metric] = List(new WMC)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileComplex.scala")
  var class1: ObjectResult = result.getClassByName("TestFileComplex").get

  test("Weighted method complexity") {
    val wmccc = class1.getMetricByName("WMCcc").get.value.toInt
    val wmcnormal = class1.getMetricByName("WMCnormal").get.value.toInt
    val wmcccinit = class1.getMetricByName("WMCccInit").get.value.toInt
    val wmcnormalinit = class1.getMetricByName("WMCnormalInit").get.value.toInt

    assert(wmccc == 21)
    assert(wmcnormal == 10)

    assert(wmcccinit == 24)
    assert(wmcnormalinit == 11)
  }
}