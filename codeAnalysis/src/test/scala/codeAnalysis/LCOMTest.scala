package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.ResultUnit
import codeAnalysis.metrics.LCOM
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 5/10/2017.
  */
class LCOMTest extends UnitSpec{
  var metrics: List[Metric] = List(new LCOM)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileLCOM.scala")

  def getClassValue(name: String, metric: String): Int = {
    result.getClassByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Empty class") {
    val className = "Test1"
    val lcom = getClassValue(className, "LCOM")
    val lcomNeg = getClassValue(className, "LCOMneg")
    assert(lcom == 0)
    assert(lcomNeg == 0)
  }

  test("Class with only one method") {
    val className = "Test2"
    val lcom = getClassValue(className, "LCOM")
    val lcomNeg = getClassValue(className, "LCOMneg")
    assert(lcom == 0)
    assert(lcomNeg == 0)
  }

  test("Class with one connected pair") {
    val className = "Test3"
    val lcom = getClassValue(className, "LCOM")
    val lcomNeg = getClassValue(className, "LCOMneg")
    assert(lcom == 0)
    assert(lcomNeg == -1)
  }

  test("Class with a disconnected pair") {
    val className = "Test4"
    val lcom = getClassValue(className, "LCOM")
    val lcomNeg = getClassValue(className, "LCOMneg")
    assert(lcom == 1)
    assert(lcomNeg == 1)
  }

  test("Class with one connected and one disconnected pair") {
    val className = "Test5"
    val lcom = getClassValue(className, "LCOM")
    val lcomNeg = getClassValue(className, "LCOMneg")
    assert(lcom == 1)
    assert(lcomNeg == 1)
  }

  test("Class with two connected pairs and var") {
    val className = "Test6"
    val lcom = getClassValue(className, "LCOM")
    val lcomNeg = getClassValue(className, "LCOMneg")
    assert(lcom == 0)
    assert(lcomNeg == -1)
  }

  test("Class with two disconnected pairs") {
    val className = "Test7"
    val lcom = getClassValue(className, "LCOM")
    val lcomNeg = getClassValue(className, "LCOMneg")
    assert(lcom == 3)
    assert(lcomNeg == 3)
  }

  test("Class with disconnected pair and no instance variables") {
    val className = "Test8"
    val lcom = getClassValue(className, "LCOM")
    val lcomNeg = getClassValue(className, "LCOMneg")
    assert(lcom == 1)
    assert(lcomNeg == 1)
  }
}
