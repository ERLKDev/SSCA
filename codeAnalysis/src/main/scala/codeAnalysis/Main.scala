package codeAnalysis

import codeAnalysis.metrics._
import main.scala.analyser.Analyser

/**
  * Created by Erik on 5-4-2017.
  */
object Main {

  def main (args: Array[String] ): Unit = {
    val a = "C:\\Users\\ErikL\\IdeaProjects\\SSCA\\codeAnalysis\\src\\main\\scala\\codeAnalysis\\Utils\\FunctionalUtil.scala"

    val metrics = List(new Complex)
    val an = new Analyser(metrics, "C:\\Users\\ErikL\\IdeaProjects\\SSCA\\codeAnalysis", 1)

    STimer.time("Analyse", println(an.analyse(a)))

    println("done")
  }

}
