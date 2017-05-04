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
  var result: ResultUnit = analyser.analyse(testRoot + "TestFile1.scala")
  var class1: ObjectResult = result.getClassByName("TestFile1").get


  test("No function statements") {
    val loc = class1.getFunctionByName("test1").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test1").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test1").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 2)
    assert(sloc == 2)
    assert(cloc == 0)
  }

  test("One function statements") {
    val loc = class1.getFunctionByName("test2").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test2").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test2").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 3)
    assert(sloc == 3)
    assert(cloc == 0)
  }

  test("Two function statements") {
    val loc = class1.getFunctionByName("test3").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test3").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test3").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 4)
    assert(sloc == 4)
    assert(cloc == 0)
  }

  test("Blank line in statements") {
    val loc = class1.getFunctionByName("test4").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test4").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test4").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 4)
    assert(sloc == 4)
    assert(cloc == 0)
  }

  test("Comment in statements") {
    val loc = class1.getFunctionByName("test5").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test5").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test5").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 5)
    assert(sloc == 4)
    assert(cloc == 1)
  }

  test("One line multi-line comment in statements") {
    val loc = class1.getFunctionByName("test6").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test6").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test6").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 5)
    assert(sloc == 4)
    assert(cloc == 1)
  }

  test("Three line multi-line comment in statements with three comment lines") {
    val loc = class1.getFunctionByName("test7").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test7").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test7").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 7)
    assert(sloc == 4)
    assert(cloc == 3)
  }

  test("Three line multi-line comment in statements with one comment line") {
    val loc = class1.getFunctionByName("test8").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test8").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test8").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 7)
    assert(sloc == 4)
    assert(cloc == 1)
  }

  test("Comment in statements on code line") {
    val loc = class1.getFunctionByName("test9").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test9").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test9").get.getMetricByName("functionCLOC").get.value.toInt
    assert(loc == 4)
    assert(sloc == 4)
    assert(cloc == 1)
  }

  test("Three line multi-line comment in statements with one comment line on code line") {
    val loc = class1.getFunctionByName("test10").get.getMetricByName("functionLOC").get.value.toInt
    val sloc = class1.getFunctionByName("test10").get.getMetricByName("functionSLOC").get.value.toInt
    val cloc = class1.getFunctionByName("test10").get.getMetricByName("functionCLOC").get.value.toInt

    assert(loc == 5)
    assert(sloc == 4)
    assert(cloc == 2)
  }
}
