package main.scala.analyser.context

import java.io.File

import main.scala.analyser.Compiler.CompilerProvider

/**
  * Created by ErikL on 4/7/2017.
  */
class ProjectContext(files: List[File]) extends CompilerProvider{
  def getFiles : List[File] = files
}
