import analyser.Compiler.CompilerProvider
import analyser.util.TreeUtil
import analyser.{Analyser, MetricRunner}
import metrics._

/**
  * Created by Erik on 5-4-2017.
  */
object Main extends CompilerProvider {
  import global._
  def main (args: Array[String] ): Unit = {
    val a = "C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src\\Test.scala"

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize)
    val an = new Analyser("C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src", metrics)

    //println(showRaw(treeFromFile(a)))
    val result = an.analyse()
    val file =result.getFile("C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src\\Test.scala")

    file.getFun

 /*   c.foreach(x => println("\n"  + x))*/

/*    println(showRaw(treeFromFile(a)))
    val c = an.analyse()
    println("startGroup")*/
/*    val g = groupResultsByObject(an.analyse())*/
    println("done")
  }

}
