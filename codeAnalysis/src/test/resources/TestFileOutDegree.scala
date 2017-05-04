class TestFileOutDegree {
  def test1(): Unit = {

  }

  def test2(): Unit = {
    a()
  }

  def test3(): Unit = {
    a()
    b()
  }

  def test4(): Unit = {
    a()
    b()
    c()
  }

  def test5(): Unit = {
    a()
    b()
    b()
  }

  def test6(): Unit = {
    a()
    a()
    b()
    b()
    c()
  }

  def test7(): Unit = {
    if(true){
      a()
    }
    a()
    b()
  }

  def test8(): Unit = {
    d(a())
    a()
  }

  def test9(): Unit = {
    g(e)
    e(5)
  }


  def a(): Unit = {
    println("a")
  }

  def b(): Unit = {}

  def c(): Unit = {}

  def d(op: Unit): Unit = {}

  def e(v: Int): Int = {}

  def g(op: Int => Int): Unit = {}
}