package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric
import main.scala.metrics._

/**
  * Created by erikl on 5/4/2017.
  */
class LOCTest extends UnitSpec {
  var metrics: List[Metric] = List(new Loc)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileLoc.scala")
  var class1: ObjectResult = result.getClassByName("TestFileLoc").get

  def getMethodMetric(name: String, metric: String): Int = {
    class1.getFunctionByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("No function statements") {
    val method = "test1"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 2)
    assert(sloc == 2)
    assert(cloc == 0)
  }

  test("One function statements") {
    val method = "test2"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 3)
    assert(sloc == 3)
    assert(cloc == 0)
  }

  test("Two function statements") {
    val method = "test3"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 4)
    assert(sloc == 4)
    assert(cloc == 0)
  }

  test("Blank line in statements") {
    val method = "test4"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 4)
    assert(sloc == 4)
    assert(cloc == 0)
  }

  test("Comment in statements") {
    val method = "test5"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 5)
    assert(sloc == 4)
    assert(cloc == 1)
  }

  test("One line multi-line comment in statements") {
    val method = "test6"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 5)
    assert(sloc == 4)
    assert(cloc == 1)
  }

  test("Three line multi-line comment in statements with three comment lines") {
    val method = "test7"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 7)
    assert(sloc == 4)
    assert(cloc == 3)
  }

  test("Three line multi-line comment in statements with one comment line") {
    val method = "test8"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 5)
    assert(sloc == 4)
    assert(cloc == 1)
  }

  test("Comment in statements on code line") {
    val method = "test9"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")
    assert(loc == 4)
    assert(sloc == 4)
    assert(cloc == 1)
  }

  test("Three line multi-line comment in statements with one comment line on code line") {
    val method = "test10"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 5)
    assert(sloc == 4)
    assert(cloc == 2)
  }

  test("Multi and single line comment connected") {
    val method = "test11"
    val loc = getMethodMetric(method, "functionLOC")
    val sloc = getMethodMetric(method, "functionSLOC")
    val cloc = getMethodMetric(method, "functionCLOC")

    assert(loc == 5)
    assert(sloc == 2)
    assert(cloc == 3)
  }

  test("Check object lines") {
    val loc = class1.getMetricByName("objectLOC").get.value.toInt
    val sloc = class1.getMetricByName("objectSLOC").get.value.toInt
    val cloc = class1.getMetricByName("objectCLOC").get.value.toInt

    assert(loc == 57)
    assert(sloc == 42)
    assert(cloc == 17)
  }
}
