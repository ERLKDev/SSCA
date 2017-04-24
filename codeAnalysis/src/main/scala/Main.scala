
import analyser.AnalyserS
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric}
import main.scala.metrics.Complex
import metrics._

/**
  * Created by Erik on 5-4-2017.
  */
object Main {

  def main (args: Array[String] ): Unit = {
    val a = "C:\\Users\\ErikL\\IdeaProjects\\SSCA\\codeAnalysis\\src\\main\\scala\\Test.scala"

    val metrics = List(new Complex)//, new WMC, new OutDegree, new PatternSize, new DIT)
    val an = new AnalyserS("C:\\Users\\ErikL\\IdeaProjects\\SSCA\\codeAnalysis", metrics)


    val objectMetricsHeader = metrics.filter(x => x.isInstanceOf[ObjectMetric])
      .asInstanceOf[List[ObjectMetric]].foldLeft(List[String]())((a, b) => a ::: b.objectHeader).sortWith((a, b) => a < b)

    val functionMetricsHeader = metrics.filter(x => x.isInstanceOf[FunctionMetric])
      .asInstanceOf[List[FunctionMetric]].foldLeft(List[String]())((a, b) => a ::: b.functionHeader).sortWith((a, b) => a < b)


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
