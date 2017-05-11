// RFC 1 (Constructor only)
class Test1 {

}

// RFC 2 (Constructor and function)
class Test2 {
  def a(): Unit = {

  }
}

// RFC 3
class Test3 {
  def a(): Unit = {

  }

  def b(): Unit = {

  }
}

// RFC 4
class Test4 {
  def a(): Unit = {
    println("test")
  }

  def b(): Unit = {

  }
}

// RFC 6
class Test5 {
  def a(): Unit = {
    println("test")
  }

  def b(): Unit = {
    val x = new Test3
    x.b()
  }
}

// RFC 8
class Test6 {
  def a(): Unit = {
    println("test")
    b()
  }

  def b(): Unit = {
    val x = new Test3
    x.b()
    a()
  }
}

// RFC 8
class Test7 {
  def a(): Unit = {
    println("test")
    b()
  }

  def b(): Unit = {
    val x = new Test3
    println("test2")
    x.b()
    a()
  }
}