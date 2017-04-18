package main.scala.metrics

import main.scala.Utils.SourceCodeUtil
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.{MetricResult, UnitType}
import main.scala.analyser.util.TreeSyntaxUtil

/**
  * Created by Erik on 14-4-2017.
  */
class NOC extends ObjectMetric with SourceCodeUtil with TreeSyntaxUtil{
  import global._

  override def objectHeader: List[String] = List("NOC")

  /**
    * Count the number of children of an object.
    * Because an object can't have children, this is always 0!
    *
    * @param tree the ast from the object
    * @param code the code from the object
    * @return
    */
  override def run(tree: global.ModuleDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Object, getName(tree), "NOC", 0))
  }


  /**
    * Count the number of children of a class or trait.
    *
    * @param tree the ast from the class or trait
    * @param code the code from the class or trait
    * @return
    */
  override def run(tree: global.ClassDef, code: List[String]): List[MetricResult] = {
    getAstNode(tree) match {
      case x: ClassDefinition =>
        List(hasChildren(tree, x.name, x.pack))

      case x: TraitDefinition =>
        List(hasChildren(tree, x.name, x.pack))
      case _ =>
        List(MetricResult(getRangePos(tree), UnitType.Object, getName(tree), "NOC", 0))

    }
  }

  /**
    * Counts the amount of children from a class or trait
    *
    * @param tree the tree of the class or trait
    * @param name the name of the class or trait
    * @param pack the pacakge of the class or trait
    * @return
    */
  private def hasChildren(tree: Tree, name: String, pack: String): MetricResult = {
    def recursive(tree: Tree): Int = getAstNode(tree) match {
      case (_: ClassDefinition | _: TraitDefinition | _: AbstractClassDefinition) =>
        val x = tree.asInstanceOf[ClassDef]
        x.symbol.parentSymbols.count(x => getObjectPackage(x) + "." + x.name.toString == pack + "." + name) + tree.children.foldLeft(0)((a,b) => a + recursive(b))

      case ObjectDefinition(x, _, _) =>
        x.symbol.parentSymbols.count(x => getObjectPackage(x) + "." + x.name.toString == pack + "." + name) + tree.children.foldLeft(0)((a,b) => a + recursive(b))
      case _ =>
        tree.children.foldLeft(0)((a,b) => a + recursive(b))
    }

    val filesToCheck = getFilesOccurrence(getContext.getFiles, name)
    val result = filesToCheck.foldLeft(0)((a, b) => a + recursive(treeFromFile(b)))
    MetricResult(getRangePos(tree), UnitType.Object, name, "NOC", result)
  }
}
