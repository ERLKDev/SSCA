package  main.scala.analyser.prerun

import java.io.File

import analyser.Compiler.CompilerS


/**
  * Created by Erik on 19-4-2017.
  */
abstract class PreRunJob(compiler: CompilerS) {
  def preRun(files: List[File]): Unit

}
