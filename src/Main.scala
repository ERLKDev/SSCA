import analyser.Compiler.CompilerProvider
import analyser.MetricRunner
import metrics.Loc

/**
  * Created by Erik on 5-4-2017.
  */
object Main extends CompilerProvider {


  def main (args: Array[String] ): Unit = {
    val a = "D:\\Master project 2017\\code\\SSCA\\src\\analyser\\MetricRunner.scala"
    val b = treeFromFile(a)
    val mrunner = new MetricRunner
    val c = mrunner.run(List(new Loc), b.asInstanceOf[mrunner.global.Tree], null)
    println(c)
    println("done")
  }
}
