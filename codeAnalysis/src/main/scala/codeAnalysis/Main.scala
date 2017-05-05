package codeAnalysis

import codeAnalysis.metrics._
import main.scala.analyser.Analyser

/**
  * Created by Erik on 5-4-2017.
  */
object Main {

  def main (args: Array[String] ): Unit = {
    val a = "C:\\tmp\\gitAkkaAkka1\\akka-testkit\\src\\main\\scala\\akka\\testkit\\javadsl\\TestKit.scala"

    val metrics = List(new Complex, new WMC, new OutDegree, new PatternSize, new DIT)
    val an = new Analyser(metrics, "C:\\tmp\\gitAkkaAkka1", 4)

    STimer.time("Analyse", println(an.analyse(a)))

    println("done")
  }

}
