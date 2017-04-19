package analyser

import java.io.File

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.prerun.PreRunJob
import main.scala.analyser.util.TreeUtil

/**
  * Created by Erik on 19-4-2017.
  */
class PreRunner extends CompilerProvider with TreeUtil{

  def run(preRunJobs : List[PreRunJob], files: List[File]): Unit = {
    preRunJobs.foreach(x => x.preRun(files))
  }
}
