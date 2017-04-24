package analyser

import java.io.File

import analyser.Compiler.CompilerS
import main.scala.analyser.prerun.PreRunJob
import main.scala.analyser.util.TreeUtil

/**
  * Created by Erik on 19-4-2017.
  */
class PreRunner(compiler: CompilerS) extends TreeUtil {

  def run(preRunJobs : List[PreRunJob], files: List[File]): Unit = {
    preRunJobs.foreach(x => x.preRun(files))
  }
}
