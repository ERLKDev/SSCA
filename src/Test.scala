/**
  * Created by Erik on 4-4-2017.
  */
object Test {
  def test(): Unit = {
    println("hoi")
    for( a <- 1 to 10){
      println( "Value of a: " + a )
    }
    // Test comment
    val l = List(1, 2, 3)

    l match {
      case Nil =>

      case x::tail =>
        println(x)
    }
    /* enkel deze */
    if (true) {

    }else if(false) {

    }

    /* double
    * line */
    while (true) {

    }
  }
}

