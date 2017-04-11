package main.scala

import main.scala.analyser.Analyser
import main.scala.analyser.Compiler.CompilerProvider
import main.scala.metrics._

/**
  * Created by Erik on 5-4-2017.
  */
object Main extends CompilerProvider {
  def main (args: Array[String] ): Unit = {
    //val a = "C:\\Users\\ErikL\\IdeaProjects\\ScalaCodeProjects\\akka"

    val metrics = List(new Loc, new Complex, new WMC, new OutDegree, new PatternSize, new DIT)
    val an = new Analyser("C:\\Users\\ErikL\\IdeaProjects\\SSCA\\", metrics)

    val result = an.analyse()


/*    val file =result.getFile("C:\\Users\\ErikL\\IdeaProjects\\SSCA\\src\\Test.scala")*/
    println(result)

 /*   c.foreach(x => println("\n"  + x))*/

/*    println(showRaw(treeFromFile(a)))
    val c = an.analyse()
    println("startGroup")*/
/*    val g = groupResultsByObject(an.analyse())*/
    println("done")
  }

}
