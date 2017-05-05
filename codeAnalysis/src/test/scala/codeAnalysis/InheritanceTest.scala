package codeAnalysis

import codeAnalysis.TestSpecs.UnitSpec
import codeAnalysis.analyser.result.ResultUnit
import codeAnalysis.metrics.Inheritance
import main.scala.analyser.Analyser
import main.scala.analyser.metric.Metric

/**
  * Created by erikl on 5/5/2017.
  */
class InheritanceTest extends UnitSpec{
  var metrics: List[Metric] = List(new Inheritance)
  var analyser: Analyser = new Analyser(metrics, testRoot, 1)
  var result: ResultUnit = analyser.analyse(testRoot + "TestFileInheritance.scala")

  def getClassValue(name: String, metric: String): Int = {
    result.getClassByName(name).get.getMetricByName(metric).get.value.toInt
  }

  test("Class extends nothing") {
    val className = "Test1"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 0)
    assert(cI == 0)
    assert(tI == 0)
  }

  test("Class extends class") {
    val className = "Test2"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 1)
    assert(cI == 1)
    assert(tI == 0)
  }

  test("Class extends class and trait") {
    val className = "Test3"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 2)
    assert(cI == 1)
    assert(tI == 1)
  }


  test("Class extends class and two traits") {
    val className = "Test4"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 3)
    assert(cI == 1)
    assert(tI == 2)
  }

  test("Class extends trait") {
    val className = "Test5"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 1)
    assert(cI == 0)
    assert(tI == 1)
  }

  test("Class extends two traits") {
    val className = "Test6"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 2)
    assert(cI == 0)
    assert(tI == 2)
  }



  test("object extends nothing") {
    val className = "Test7"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 0)
    assert(cI == 0)
    assert(tI == 0)
  }

  test("object extends class") {
    val className = "Test8"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 1)
    assert(cI == 1)
    assert(tI == 0)
  }

  test("object extends class and trait") {
    val className = "Test9"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 2)
    assert(cI == 1)
    assert(tI == 1)
  }


  test("object extends class and two traits") {
    val className = "Test10"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 3)
    assert(cI == 1)
    assert(tI == 2)
  }

  test("object extends trait") {
    val className = "Test11"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 1)
    assert(cI == 0)
    assert(tI == 1)
  }

  test("object extends two traits") {
    val className = "Test12"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 2)
    assert(cI == 0)
    assert(tI == 2)
  }


  test("trait extends nothing") {
    val className = "Test13"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 0)
    assert(cI == 0)
    assert(tI == 0)
  }

  test("trait extends class") {
    val className = "Test14"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 1)
    assert(cI == 1)
    assert(tI == 0)
  }

  test("trait extends class and trait") {
    val className = "Test15"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 2)
    assert(cI == 1)
    assert(tI == 1)
  }


  test("trait extends class and two traits") {
    val className = "Test16"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 3)
    assert(cI == 1)
    assert(tI == 2)
  }

  test("trait extends trait") {
    val className = "Test17"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 1)
    assert(cI == 0)
    assert(tI == 1)
  }

  test("trait extends two traits") {
    val className = "Test18"
    val i = getClassValue(className, "Inheritance")
    val cI = getClassValue(className, "ClassInheritance")
    val tI = getClassValue(className, "TraitInheritance")
    assert(i == 2)
    assert(cI == 0)
    assert(tI == 2)
  }


}
