package codeAnalysis.metrics

import java.io.File

import codeAnalysis.analyser.AST._
import main.scala.Utils.SourceCodeUtil
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 5/10/2017.
  */
class NOC extends ObjectMetric with SourceCodeUtil{
  override def objectHeader: List[String] = List("NOC")

  override def run(tree: ObjectDefinition, code: List[String]): List[MetricResult] = {
    List(new MetricResult(tree.pos, tree.name, "NOC", 0))
  }

  override def run(tree: ClassDefinition, code: List[String]): List[MetricResult] = {
    val noc = hasChildren(getFilesOccurrence(getContext.getFiles, tree.name), tree.name, tree.pack)
    List(new MetricResult(tree.pos, tree.name, "NOC", noc))
  }

  override def run(tree: TraitDefinition, code: List[String]): List[MetricResult] = {
    val noc = hasChildren(getFilesOccurrence(getContext.getFiles, tree.name), tree.name, tree.pack)
    List(new MetricResult(tree.pos, tree.name, "NOC", noc))
  }


  /**
    * Counts the amount of children from a class or trait
    *
    * @param name the name of the class or trait
    * @param pack the pacakge of the class or trait
    * @return
    */
  private def hasChildren(possibleFiles: List[File], name: String, pack: String): Int = {
    def recursive(tree: AST): Int = tree match {
      case x: Module =>
        val n = x.parents.foldLeft(0){
          (a, b) =>
            val pName = b.pack + b.name
            if (pName == pack + name)
              a + 1
            else
              a
        }
        tree.children.foldLeft(n)((a,b) => a + recursive(b))

      case _ =>
        tree.children.foldLeft(0)((a,b) => a + recursive(b))
    }

    possibleFiles.foldLeft(0){
      (a, b) =>
        val c = if (getContext.isCached(b)) getContext.getCached(b).get else getContext.compiler.treeFromFile(b)
        if (c == null) {
          a
        }else {
          getContext.addFileToCache(b)
          getContext.addPreCompiledFile(b, c)
          a + recursive(c)
        }
    }
  }
}
