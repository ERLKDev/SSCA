package codeAnalysis.TestSpecs

import main.scala.analyser.util.ResultUtil
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

/**
  * Created by erikl on 5/4/2017.
  */
abstract class UnitSpec extends FunSuite with Matchers with BeforeAndAfter with ResultUtil {
  val testRoot: String = ".\\codeAnalysis\\src\\test\\resources\\"
}


