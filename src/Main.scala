import analyser.Compiler.CompilerProvider
import analyser.util.PrintUtil
import analyser.{Analyser, MetricRunner}

/**
  * Created by Erik on 5-4-2017.
  */
object Main extends CompilerProvider with PrintUtil with ResultUtil{
  import global._
  def main (args: Array[String] ): Unit = {
    val a = "C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src\\Test.scala"

    val an = new Analyser("C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src")
    val c = an.analyse(a).getObjects
    c.foreach(x => println("\n"  + x))

/*    println(showRaw(treeFromFile(a)))
    val c = an.analyse()
    println("startGroup")*/
/*    val g = groupResultsByObject(an.analyse())*/
    println("done")
  }
}
