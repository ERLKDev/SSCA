package codeAnalysis

/**
  * Created by erikl on 5/5/2017.
  */
object STimer {
  def time[R](name: String, block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println(name + "  = Done in: " + (t1 - t0) + "ns (" + ((t1 - t0).toDouble / 1000000000.0) + "seconds)")
    result
  }
}
