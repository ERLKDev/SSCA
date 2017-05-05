package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric
import main.scala.metrics.Complex

/**
  * Created by erikl on 5/4/2017.
  */
class ComplexTest extends UnitSpec{
  var metrics: List[Metric] = List(new Complex)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileComplex.scala")
  var class1: ObjectResult = result.getClassByName("TestFileComplex").get

  def getMethodMetric(name: String, metric: String): Int = {
    class1.getFunctionByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("No function statements") {
    val method = "test1"
    val cc = getMethodMetric(method, "CC")

    assert(cc == 1)
  }

  test("Function statements") {
    val method = "test2"
    val cc = getMethodMetric(method, "CC")

    assert(cc == 1)
  }

  test("If statement") {
    val method = "test3"
    val cc = getMethodMetric(method, "CC")

    assert(cc == 2)
  }

  test("If else statement") {
    val method = "test4"
    val cc = getMethodMetric(method, "CC")

    assert(cc == 2)
  }

  test("If else if statement") {
    val method = "test5"
    val cc = getMethodMetric(method, "CC")

    assert(cc == 3)
  }

  test("While statement") {
    val method = "test6"
    val cc = getMethodMetric(method, "CC")

    assert(cc == 2)
  }

  test("Do while statement") {
    val method = "test7"
    val cc = getMethodMetric(method, "CC")

    assert(cc == 2)
  }

  test("Switch statement") {
    val method = "test8"
    val cc = getMethodMetric(method, "CC")

    assert(cc == 4)
  }

  test("Nested functions") {
    val method = "test9"
    val cc = getMethodMetric(method, "CC")
    val ccNested = class1.getFunctionByName(method).get.getFunctionByName(method + "Nested").get.getMetricByName("CC").get.value.toInt

    assert(cc == 2)
    assert(ccNested == 2)
  }
}
