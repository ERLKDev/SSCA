package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.{ObjectResult, ResultUnit}
import codeAnalysis.metrics.{DON, PatternSize}
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 5/15/2017.
  */
class DONTest extends UnitSpec{
  var metrics: List[Metric] = List(new DON, new PatternSize)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileDON.scala")
  var class1: ObjectResult = result.getClassByName("TestFileDON").get

  def getMethodMetric(name: String, metric: String): Int = {
    class1.getFunctionByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Empty function") {
    val method = "test1"
    val don = getMethodMetric(method, "DON")
    val psiz = getMethodMetric(method, "PatternSize")

    assert(don == psiz)
    assert(don > 0)
  }

  test("if statement") {
    val method = "test2"
    val don = getMethodMetric(method, "DON")
    val psiz = getMethodMetric(method, "PatternSize")

    assert(don < psiz)
    assert(don > 0)
  }

  test("nested if statement (additon on test2)") {
    val method1 = "test2"
    val don1 = getMethodMetric(method1, "DON")
    val psiz1 = getMethodMetric(method1, "PatternSize")

    val method2 = "test3"
    val don2 = getMethodMetric(method2, "DON")
    val psiz2 = getMethodMetric(method2, "PatternSize")

    assert(don1 < psiz1)
    assert(don1 > 0)

    assert(don2 < psiz2)
    assert(don2 > 0)

    assert(don1 < don2)
  }


  test("nested if statement same size, different branch (additon on test3)") {
    val method1 = "test3"
    val don1 = getMethodMetric(method1, "DON")
    val psiz1 = getMethodMetric(method1, "PatternSize")

    val method2 = "test4"
    val don2 = getMethodMetric(method2, "DON")
    val psiz2 = getMethodMetric(method2, "PatternSize")

    assert(don1 < psiz1)
    assert(don1 > 0)

    assert(don2 < psiz2)
    assert(don2 > 0)

    assert(don1 == don2)
  }
}
