package codeAnalysis.analyser

import java.io.File

import codeAnalysis.analyser.Compiler.CompilerS
import main.scala.analyser.prerun.PreRunJob

/**
  * Created by Erik on 19-4-2017.
  */
class PreRunner(compiler: CompilerS){

  def run(preRunJobs : List[PreRunJob], files: List[File]): Unit = {
    preRunJobs.foreach(x => x.preRun(files))
  }
}
