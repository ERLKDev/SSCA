// LCOM 0 (P=0, Q=0)
class Test1 {

}

// LCOM 0 (P=0, Q=0)
class Test2 {
  val a: Int = 0

  def x: Unit = {
    println(a + "23")
  }
}


// LCOM 0 (P=0, Q=1)
class Test3 {
  val a: Int = 0

  def x: Unit = {
    println(a)
  }

  def y: Unit = {
    println(a)
  }
}

// LCOM 1 (P=1, Q=0)
class Test4 {
  val a: Int = 0
  val b: Int = 0

  def x: Unit = {
    println(a)
  }

  def y: Unit = {
    println(b)
  }
}

// LCOM 1 (P=2, Q=1)
class Test5 {
  val a: Int = 0
  val b: Int = 0

  def x: Unit = {
    println(a)
  }

  def y: Unit = {
    println(a)
  }

  def z: Unit = {
    println(b)
  }
}

// LCOM 0 (P=1, Q=2)
class Test6 {
  val a: Int = 0
  var b: Int = 0

  def x: Unit = {
    println(a)
  }

  def y: Unit = {
    println(a)
    b = 18
  }

  def z: Unit = {
    println(b)
  }
}

// LCOM 3 (P=3, Q=0)
class Test7 {
  val a: Int = 0
  var b: Int = 0
  val c: Int = 0

  def x: Unit = {
    println(a)
  }

  def y: Unit = {
    b = 18
  }

  def z: Unit = {
    println(c)
  }
}

// LCOM 1 (P=1, Q=0)
class Test8 {

  def x: Unit = {
    println("x")
  }

  def y: Unit = {
    println("y")
  }
}