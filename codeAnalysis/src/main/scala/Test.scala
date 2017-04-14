package main.scala

import main.scala.analyser.util.TreeSyntaxUtil

/**
  * Created by Erik on 13-4-2017.
  */
class Test extends TreeSyntaxUtil {
  def test(): Unit = {
    for( a <- 1 to 3; b <- 1 to 3){
      println( "Value of a: " + a );
      println( "Value of b: " + b );
    }
  }
}
