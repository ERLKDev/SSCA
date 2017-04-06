import analyser.Compiler.CompilerProvider
import analyser.util.{PrintUtil, TreeUtil}
import analyser.{Analyser, MetricRunner}
import metrics.{Complex, Loc, WMC}

/**
  * Created by Erik on 5-4-2017.
  */
object Main extends CompilerProvider with PrintUtil{
  import global._
  def main (args: Array[String] ): Unit = {
    val a = "C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src\\Test.scala"

    val metrics = List(new Loc, new Complex, new WMC)
    val an = new Analyser("C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src", metrics)


    println(an.analyse(a))
 /*   c.foreach(x => println("\n"  + x))*/

/*    println(showRaw(treeFromFile(a)))
    val c = an.analyse()
    println("startGroup")*/
/*    val g = groupResultsByObject(an.analyse())*/
    println("done")
  }
}
