package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric
import main.scala.metrics.OutDegree

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
    val out = getMethodMetric("test1", "OutDegree")
    val outDistinct = getMethodMetric("test1", "OutDegreeDistinct")

    assert(out == 0)
    assert(outDistinct == 0)
  }

  test("One function call") {
    val out = getMethodMetric("test2", "OutDegree")
    val outDistinct = getMethodMetric("test2", "OutDegreeDistinct")

    assert(out == 1)
    assert(outDistinct == 1)
  }

  test("Two function calls") {
    val out = getMethodMetric("test3", "OutDegree")
    val outDistinct = getMethodMetric("test3", "OutDegreeDistinct")

    assert(out == 2)
    assert(outDistinct == 2)
  }

  test("Three function calls") {
    val out = getMethodMetric("test4", "OutDegree")
    val outDistinct = getMethodMetric("test4", "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 3)
  }

  test("Two Distinct function call") {
    val out = getMethodMetric("test5", "OutDegree")
    val outDistinct = getMethodMetric("test5", "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 2)
  }

  test("Three Distinct function calls") {
    val out = getMethodMetric("test6", "OutDegree")
    val outDistinct = getMethodMetric("test6", "OutDegreeDistinct")

    assert(out == 5)
    assert(outDistinct == 3)
  }

  test("Nested function function calls") {
    val out = getMethodMetric("test7", "OutDegree")
    val outDistinct = getMethodMetric("test7", "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 2)
  }

  test("Function call as argument") {
    val out = getMethodMetric("test8", "OutDegree")
    val outDistinct = getMethodMetric("test8", "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 2)
  }

  test("Higher order function as argument") {
    val out = getMethodMetric("test9", "OutDegree")
    val outDistinct = getMethodMetric("test9", "OutDegreeDistinct")

    assert(out == 3)
    assert(outDistinct == 2)
  }

}
