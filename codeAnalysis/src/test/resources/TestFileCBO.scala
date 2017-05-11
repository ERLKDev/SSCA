// CBO 1
class CBOtest1 {

}

// CBO 2
class CBOtest2 {

  def a(): Unit = {
    val x = new CBO2a()
  }
}

// CBO 2
class CBOtest3 {

}

// CBO 1
class CBOtest4 extends CBO4a {

}


// CBO 2
class CBOtest5 {

}

// CBO 2
class CBOtest6 {
  CBO6a.a()
}


class CBO2a {

}

class CBO3a {
  val x = new CBOtest3
}

class CBO4a {

}

class CBO5a extends CBOtest5 {

}


object CBO6a {
  def a() : Unit = {

  }
}