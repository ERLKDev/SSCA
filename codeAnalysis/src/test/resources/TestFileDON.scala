class TestFileDON{

  def test1 = {
    println(5)
  }

  def test2 = {
    println(5)
    if (true) {

    }else {

    }
  }

  def test3 = {
    println(5)
    if (true) {
      if (false) {
        println(8)
      }
    }else {

    }
  }

  def test4 = {
    println(5)
    if (true) {

    }else {
      if (false) {
        println(8)
      }
    }
  }
}