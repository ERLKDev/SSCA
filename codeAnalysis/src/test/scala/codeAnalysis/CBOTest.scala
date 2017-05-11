package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.ResultUnit
import codeAnalysis.metrics.CBO
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 5/11/2017.
  */
class CBOTest extends UnitSpec{
  var metrics: List[Metric] = List(new CBO)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileCBO.scala")

  def getClassValue(name: String, metric: String): Int = {
    result.getClassByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Empty class") {
    val className = "CBOtest1"
    val cbo = getClassValue(className, "CBO")
    assert(cbo == 1)
  }

  test("Class coupled with another class (this -> that)") {
    val className = "CBOtest2"
    val rfc = getClassValue(className, "CBO")
    assert(rfc == 2)
  }

  test("Class coupled with another class (that -> this)") {
    val className = "CBOtest3"
    val rfc = getClassValue(className, "CBO")
    assert(rfc == 2)
  }

  test("Class coupled with another class trough extend (this -> that)") {
    val className = "CBOtest4"
    val rfc = getClassValue(className, "CBO")
    assert(rfc == 1)
  }

  test("Class coupled with another class trough extend (that -> this)") {
    val className = "CBOtest5"
    val rfc = getClassValue(className, "CBO")
    assert(rfc == 2)
  }

  test("Class uses Object function") {
    val className = "CBOtest6"
    val rfc = getClassValue(className, "CBO")
    assert(rfc == 2)
  }
}
