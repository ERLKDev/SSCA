package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import codeAnalysis.metrics.NPVS
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 5/15/2017.
  */
class NPVSTest extends UnitSpec{
  var metrics: List[Metric] = List(new NPVS)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileNPVS.scala")
  var class1: ObjectResult = result.getClassByName("TestFileNPVS").get

  def getMethodMetric(name: String, metric: String): Int = {
    class1.getFunctionByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Empty function") {
    val method = "test1"
    val npvs = getMethodMetric(method, "NPVS")
    val npvsMatch = getMethodMetric(method, "NPVSmatch")
    val npvsMatchParms = getMethodMetric(method, "NPVSmatchParms")

    assert(npvs == 0)
    assert(npvsMatch == 0)
    assert(npvsMatchParms == 0)
  }

  test("Function with one val definition") {
    val method = "test2"
    val npvs = getMethodMetric(method, "NPVS")
    val npvsMatch = getMethodMetric(method, "NPVSmatch")
    val npvsMatchParms = getMethodMetric(method, "NPVSmatchParms")

    assert(npvs == 1)
    assert(npvsMatch == 0)
    assert(npvsMatchParms == 0)
  }

  test("Function with one val parameter") {
    val method = "test3"
    val npvs = getMethodMetric(method, "NPVS")
    val npvsMatch = getMethodMetric(method, "NPVSmatch")
    val npvsMatchParms = getMethodMetric(method, "NPVSmatchParms")

    assert(npvs == 1)
    assert(npvsMatch == 0)
    assert(npvsMatchParms == 1)
  }

  test("Function with match") {
    val method = "test4"
    val npvs = getMethodMetric(method, "NPVS")
    val npvsMatch = getMethodMetric(method, "NPVSmatch")
    val npvsMatchParms = getMethodMetric(method, "NPVSmatchParms")

    assert(npvs == 2)
    assert(npvsMatch == 2)
    assert(npvsMatchParms == 2)
  }


  test("Function with match and param") {
    val method = "test5"
    val npvs = getMethodMetric(method, "NPVS")
    val npvsMatch = getMethodMetric(method, "NPVSmatch")
    val npvsMatchParms = getMethodMetric(method, "NPVSmatchParms")

    assert(npvs == 3)
    assert(npvsMatch == 2)
    assert(npvsMatchParms == 3)
  }
}
