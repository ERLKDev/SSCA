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

  test("Class extends anyVal") {
    val dit = getClassValue("Test1", "DIT")
    assert(dit == 1)
  }

  test("Class extends A->AnyVal") {
    val dit = getClassValue("Test2", "DIT")
    assert(dit == 2)
  }

  test("Class extends B->C->AnyVal") {
    val dit = getClassValue("Test3", "DIT")
    assert(dit == 3)
  }

  test("Class extends D(trait)->Anyval") {
    val dit = getClassValue("Test4", "DIT")
    assert(dit == 1)
  }

  test("Class extends (A->Anyval, D(trait)->Anyval)") {
    val dit = getClassValue("Test5", "DIT")
    assert(dit == 2)
  }

  test("Class extends (D(trait)->Anyval, E(trait)->Anyval))") {
    val dit = getClassValue("Test6", "DIT")
    assert(dit == 1)
  }

  test("Class extends (A->Anyval, F(trait)->(D(trait)->Anyval), E(trait)->Anyval))") {
    val dit = getClassValue("Test7", "DIT")
    assert(dit == 2)
  }

  test("Object extends Anyval") {
    val dit = getClassValue("Test8", "DIT")
    assert(dit == 1)
  }

  test("Object extends A->Anyval") {
    val dit = getClassValue("Test9", "DIT")
    assert(dit == 2)
  }

  test("Trait extends Anyval") {
    val dit = getClassValue("Test10", "DIT")
    assert(dit == 1)
  }

  test("Trait extends A->Anyval") {
    val dit = getClassValue("Test11", "DIT")
    assert(dit == 2)
  }
}
