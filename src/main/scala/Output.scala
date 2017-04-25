import java.io.{File, FileWriter}

/**
  * Created by erikl on 4/21/2017.
  */
object Output {
  protected def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }

  /**
    * Writes the output to a file
    * @param results a list of csv rows
    * @param path the destination
    */
  def writeOutput(results: List[String], path: String): Unit = {
    val fw = new FileWriter(path, true) ;
    fw.write(results.mkString("\n") + "\n") ;
    fw.flush()
    fw.close()
  }
}
