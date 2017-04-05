import analyser.Compiler.CompilerProvider
import analyser.util.PrintUtil
import analyser.{Analyser, MetricRunner}
import metrics.Loc

/**
  * Created by Erik on 5-4-2017.
  */
object Main extends CompilerProvider with PrintUtil{
  import global._

  def main (args: Array[String] ): Unit = {
    val a = "D:\\Master project 2017\\code\\SSCA\\src\\Test.scala"

    val an = new Analyser("D:\\Master project 2017\\code\\SSCA\\src")
    printresults(an.analyse(a))
    println()
    printresults(an.analyse())
    println("done")
  }
}
