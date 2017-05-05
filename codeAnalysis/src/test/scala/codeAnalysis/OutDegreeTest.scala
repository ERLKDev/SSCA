package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric
import codeAnalysis.metrics.OutDegree

/**
  * Created by erikl on 5/4/2017.
  */
class OutDegreeTest extends UnitSpec{
  var metrics: List[Metric] = List(new OutDegree)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileOutDegree.scala")
  var class1: ObjectResult = result.getClassByName("TestFileOutDegree").get

  def getMethodMetric(name: String, metric: String): Int = {
    class1.getFunctionByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("No function calls") {
    val method = "test1"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 0)
    assert(outDistinct == 0)
  }

  test("One function call") {
    val method = "test2"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 1)
    assert(outDistinct == 1)
  }

  test("Two function calls") {
    val method = "test3"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 2)
    assert(outDistinct == 2)
  }

  test("Three function calls") {
    val method = "test4"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 3)
  }

  test("Two Distinct function call") {
    val method = "test5"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 2)
  }

  test("Three Distinct function calls") {
    val method = "test6"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 5)
    assert(outDistinct == 3)
  }

  test("Nested function function calls") {
    val method = "test7"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 2)
  }

  test("Function call as argument") {
    val method = "test8"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 2)
  }

  test("Higher order function as argument") {
    val method = "test9"
    val out = getMethodMetric(method, "OutDegree")
    val outDistinct = getMethodMetric(method, "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 2)
  }

}
