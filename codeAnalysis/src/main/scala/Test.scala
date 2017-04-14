package main.scala
/**
  * Created by Erik on 13-4-2017.
  */
abstract class Test {
  val a: List[Int] = List[Int]()

  def test(): Unit = {
    println("test")
  }
}
