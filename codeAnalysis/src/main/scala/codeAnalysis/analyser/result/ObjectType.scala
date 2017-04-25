package codeAnalysis.analyser.result

/**
  * Created by erikl on 4/20/2017.
  */
object ObjectType extends Enumeration {
  type ObjectType = Value
  val ClassT = Value("class")
  val TraitT = Value("trait")
  val ObjectT = Value("object")
}
