package  main.scala.analyser.prerun

import java.io.File

/**
  * Created by Erik on 19-4-2017.
  */
trait PreRunJob {
  def preRun(files: List[File]): Unit

}
