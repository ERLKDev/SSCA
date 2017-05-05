class TestFileLoc {

  println("test")
  //Single line comment



  /* Multi
   * line
   * comment*/

  /*
  * Single multi line comment
  */

  def test1(): Unit = {

  }

  def test2(): Unit = {
    println(1)
  }

  def test3(): Unit = {
    println(1)
    println(1)
  }

  def test4(): Unit = {
    println(1)

    println(1)
  }

  def test5(): Unit = {
    println(1)

    //Comment
    println(1)
  }

  def test6(): Unit = {
    println(1)

    /* Comment*/
    println(1)
  }

  def test7(): Unit = {
    println(1)

    /* Multi
    * Line
    * Comment*/
    println(1)
  }

  def test8(): Unit = {
    println(1)

    /*
    * multi line single
    */
    println(1)
  }

  def test9(): Unit = {
    println(1) // Same Line

    println(1)
  }

  def test10(): Unit = {
    println(1) /* multi
      line */

    println(1)
  }

  def test11(): Unit = {
    /* multi
     * line */
    //Connected

  }

}