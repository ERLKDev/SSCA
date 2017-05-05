class TestFileComplex {


  if(true) {
    println("test")
  }

  if(false) {
    println("test")
  }


  // 1
  def test1(): Unit = {

  }

  // 1
  def test2(): Unit = {
    println("test")
  }


  // 2
  def test3(): Unit = {
    if (true) {
    	println("test")
    }
  }

  // 2
  def test4(): Unit = {
    if (true) {
      println("test")
    }else{
      println("test2")
    }
  }

  // 3
  def test5(): Unit = {
    if (true) {
      println("test")
    }else if (true) {
      println("test1")
    }else{
      println("test2")
    }
  }

  // 2
  def test6(): Unit = {
    while (true) {
      println("test")
    }
  }

  // 2
  def test7(): Unit = {
    do {
      println("test")
    }while(true)
  }

  // 4
  def test8(): Unit = {
    val x = _

    x match {
      case y: Int =>

      case y: Float =>

      case _ =>

    }
  }

  // 2
  def test9(): Unit = {
    // 2
    def test9Nested(): Unit = {
      if (true) {
        println("test1")
      }
    }

    if (true) {
      println("test2")
    }
  }
}