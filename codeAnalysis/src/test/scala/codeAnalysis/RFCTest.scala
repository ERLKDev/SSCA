package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.ResultUnit
import codeAnalysis.metrics.RFC
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 5/11/2017.
  */
class RFCTest extends UnitSpec{
  var metrics: List[Metric] = List(new RFC)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileRFC.scala")

  def getClassValue(name: String, metric: String): Int = {
    result.getClassByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Empty class with empty constructor") {
    val className = "Test1"
    val rfc = getClassValue(className, "RFC")
    assert(rfc == 1)
  }

  test("Class with function (and empty constructor)") {
    val className = "Test2"
    val rfc = getClassValue(className, "RFC")
    assert(rfc == 2)
  }

  test("Class with two functions (and empty constructor)") {
    val className = "Test3"
    val rfc = getClassValue(className, "RFC")
    assert(rfc == 3)
  }


  test("Class with two functions and function call(and empty constructor)") {
    val className = "Test4"
    val rfc = getClassValue(className, "RFC")
    assert(rfc == 4)
  }

  test("Class with new class") {
    val className = "Test5"
    val rfc = getClassValue(className, "RFC")
    assert(rfc == 6)
  }


  test("Class with same function names but different owners") {
    val className = "Test6"
    val rfc = getClassValue(className, "RFC")
    assert(rfc == 8)
  }

  test("Distinct function calls") {
    val className = "Test7"
    val rfc = getClassValue(className, "RFC")
    assert(rfc == 8)
  }

}
