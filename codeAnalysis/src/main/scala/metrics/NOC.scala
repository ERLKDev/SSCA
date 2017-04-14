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

  override def run(tree: global.ModuleDef, code: List[String]): List[MetricResult] = {
    List(MetricResult(getRangePos(tree), UnitType.Object, getName(tree), "NOC", 0))
  }

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

  def hasChildren(tree: Tree, name: String, pack: String): MetricResult = {
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
