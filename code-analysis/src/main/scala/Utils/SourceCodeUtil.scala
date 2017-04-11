package main.scala.Utils

/**
  * Created by Erik on 5-4-2017.
  */
trait SourceCodeUtil {
  def removeWhiteLines(code: List[String]): List[String] = {
    code.filter(s => !"""(?m)^\s+$""".r.pattern.matcher(s).matches())
  }

  def removeComments(code: List[String]): List[String] = {
    val newcode = code.filter(s => !"""(.*\/\*(.|\n)*?\*\/)""".r.pattern.matcher(s).matches())
    removeWhiteLines(newcode)
  }
}
