package  main.scala.analyser.prerun

import java.io.File

import main.scala.analyser.Compiler.CompilerProvider

/**
  * Created by Erik on 19-4-2017.
  */
trait PreRunJob extends CompilerProvider{
  def preRun(files: List[File]): Unit

}
