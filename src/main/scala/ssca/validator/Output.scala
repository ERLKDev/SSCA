package ssca.validator

import java.io.{File, FileWriter}

/**
  * Created by erikl on 4/21/2017.
  */
class Output(path: String, reset: Boolean) {
  if(reset) {
    val file = new File(path)
    file.delete()
    file.createNewFile()
  }

  val writer = new FileWriter(path, true)

  /**
    * Writes the output to a file
    * @param results a list of csv rows
    */
  def writeOutput(results: List[String]): Unit = {
    writer.write(results.mkString("\n") + "\n") ;
    writer.flush()
  }

  def close(): Unit = {
    writer.close()
  }
}
