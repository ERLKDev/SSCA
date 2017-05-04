package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.ResultUnit
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric
import main.scala.metrics.DIT

/**
  * Created by erikl on 5/4/2017.
  */
class DitTest extends UnitSpec{
  var metrics: List[Metric] = List(new DIT)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileDit.scala")

  def getClassValue(name: String, metric: String): Int = {
    result.getClassByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Dit extends anyVal") {
    val dit = getClassValue("Test1", "DIT")
    assert(dit == 1)
  }

  test("Dit extends A->AnyVal") {
    val dit = getClassValue("Test2", "DIT")
    assert(dit == 2)
  }

  test("Dit extends B->C->AnyVal") {
    val dit = getClassValue("Test3", "DIT")
    assert(dit == 3)
  }

  test("Dit extends D(trait)->Anyval") {
    val dit = getClassValue("Test4", "DIT")
    assert(dit == 1)
  }

  test("Dit extends (A->Anyval, D(trait)->Anyval)") {
    val dit = getClassValue("Test5", "DIT")
    assert(dit == 2)
  }

  test("Dit extends (D(trait)->Anyval, E(trait)->Anyval))") {
    val dit = getClassValue("Test6", "DIT")
    assert(dit == 1)
  }

  test("Dit extends (A->Anyval, F(trait)->(D(trait)->Anyval), E(trait)->Anyval))") {
    val dit = getClassValue("Test7", "DIT")
    assert(dit == 2)
  }
}
