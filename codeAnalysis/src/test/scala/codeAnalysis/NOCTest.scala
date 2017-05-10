package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.ResultUnit
import codeAnalysis.metrics.NOC
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 5/10/2017.
  */
class NOCTest extends UnitSpec{
  var metrics: List[Metric] = List(new NOC)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileNOC.scala")

  def getClassValue(name: String, metric: String): Int = {
    result.getClassByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Class is extended by nothing") {
    val className = "NOCTest1"
    val noc = getClassValue(className, "NOC")
    assert(noc == 0)
  }

  test("Class is extended by one class") {
    val className = "NOCTest2"
    val noc = getClassValue(className, "NOC")
    assert(noc == 1)
  }

  test("Class is extended by two classes") {
    val className = "NOCTest3"
    val noc = getClassValue(className, "NOC")
    assert(noc == 2)
  }

  test("Class is extended by one class which is extended by another") {
    val className = "NOCTest4"
    val noc = getClassValue(className, "NOC")
    assert(noc == 1)
  }

  test("Class is extended by one object") {
    val className = "NOCTest5"
    val noc = getClassValue(className, "NOC")
    assert(noc == 1)
  }

  test("Class is extended by two objects") {
    val className = "NOCTest6"
    val noc = getClassValue(className, "NOC")
    assert(noc == 2)
  }

  test("Class is extended by one trait") {
    val className = "NOCTest7"
    val noc = getClassValue(className, "NOC")
    assert(noc == 1)
  }

  test("Class is extended by two traits") {
    val className = "NOCTest8"
    val noc = getClassValue(className, "NOC")
    assert(noc == 2)
  }




  test("Trait is extended by nothing") {
    val className = "NOCTest9"
    val noc = getClassValue(className, "NOC")
    assert(noc == 0)
  }

  test("Trait is extended by one class") {
    val className = "NOCTest10"
    val noc = getClassValue(className, "NOC")
    assert(noc == 1)
  }

  test("Trait is extended by two classes") {
    val className = "NOCTest11"
    val noc = getClassValue(className, "NOC")
    assert(noc == 2)
  }

  test("Trait is extended by one class which is extended by another") {
    val className = "NOCTest12"
    val noc = getClassValue(className, "NOC")
    assert(noc == 1)
  }

  test("Trait is extended by one object") {
    val className = "NOCTest13"
    val noc = getClassValue(className, "NOC")
    assert(noc == 1)
  }

  test("Trait is extended by two objects") {
    val className = "NOCTest14"
    val noc = getClassValue(className, "NOC")
    assert(noc == 2)
  }

  test("Trait is extended by one trait") {
    val className = "NOCTest15"
    val noc = getClassValue(className, "NOC")
    assert(noc == 1)
  }

  test("Trait is extended by two traits") {
    val className = "NOCTest16"
    val noc = getClassValue(className, "NOC")
    assert(noc == 2)
  }



}
