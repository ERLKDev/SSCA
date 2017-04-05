package analyser.Compiler

import scala.reflect.internal.util.SourceFile

/**
  * Created by Erik on 5-4-2017.
  *
  * Wrapper class to make it easier to parse in java
  */
object Parser extends CompilerProvider{
  import global._

  def parse(file : SourceFile): Tree = {
    treeFromFile(file)
  }

  def parse(file : String) : Tree = {
    treeFromFile(file)
  }
}
