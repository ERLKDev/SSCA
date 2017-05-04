package codeAnalysis

import main.scala.analyser.Analyser
import main.scala.metrics._

/**
  * Created by Erik on 5-4-2017.
  */
object Main {

  def main (args: Array[String] ): Unit = {
    val a = "C:\\tmp\\gitAkkaAkka1\\akka-testkit\\src\\main\\scala\\akka\\testkit\\javadsl\\TestKit.scala"

    val metrics = List(new Complex, new WMC, new OutDegree, new PatternSize, new DIT)
    val an = new Analyser(metrics, "C:\\tmp\\gitAkkaAkka1", 4)

    time {println(an.analyse(a))}

    println("done")
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
  }

}
