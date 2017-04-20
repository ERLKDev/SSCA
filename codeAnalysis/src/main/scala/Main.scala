package main.scala

import main.scala.analyser.Analyser
import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.metric.{FunctionMetric, ObjectMetric}
import metrics._

/**
  * Created by Erik on 5-4-2017.
  */
object Main extends CompilerProvider {
  import global._
  def main (args: Array[String] ): Unit = {
    val a = "C:\\Users\\ErikL\\IdeaProjects\\SSCA\\codeAnalysis\\src\\main\\scala\\Test.scala"

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT)
    val an = new Analyser("C:\\Users\\ErikL\\IdeaProjects\\SSCA", metrics)


    val objectMetricsHeader = metrics.filter(x => x.isInstanceOf[ObjectMetric])
      .asInstanceOf[List[ObjectMetric]].foldLeft(List[String]())((a, b) => a ::: b.objectHeader).sortWith((a, b) => a < b).mkString(", ")

    val functionMetricsHeader = metrics.filter(x => x.isInstanceOf[FunctionMetric])
      .asInstanceOf[List[FunctionMetric]].foldLeft(List[String]())((a, b) => a ::: b.functionHeader).sortWith((a, b) => a < b).mkString(", ")

    val result = an.analyse().foldLeft("")((a, b) => a + "\n" + b.toCsvObjectSum.mkString("\n"))


/*    val file =result.getFile("C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src\\Test.scala")*/
    println(objectMetricsHeader + ", " + functionMetricsHeader)
    println(result)

 /*   c.foreach(x => println("\n"  + x))*/

/*    println(showRaw(treeFromFile(a)))
    val c = an.analyse()
    println("startGroup")*/
/*    val g = groupResultsByObject(an.analyse())*/
    println("done")
  }

}
