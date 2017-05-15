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

  test("No function statements") {
    val method = "test1"
    val npvs = getMethodMetric(method, "NPVS")
    val npvsMatch = getMethodMetric(method, "NPVSmatch")

    assert(npvs == 1)
    assert(npvsMatch == 1)
  }
}
