package codeAnalysis.metrics

import codeAnalysis.analyser.AST._
import main.scala.Utils.SourceCodeUtil
import main.scala.analyser.metric.ObjectMetric
import main.scala.analyser.result.MetricResult

/**
  * Created by erikl on 5/11/2017.
  */
class CBO extends ObjectMetric with SourceCodeUtil{
  override def objectHeader: List[String] = List("CBO")

  override def run(tree: ObjectDefinition, code: List[String]): List[MetricResult] = {
    val noc = (getCoupledModules(tree) ::: isCoupledTo(tree)).distinct.length
    List(new MetricResult(tree.pos, tree.name, "CBO", noc))
  }

  override def run(tree: ClassDefinition, code: List[String]): List[MetricResult] = {
    val noc = (getCoupledModules(tree) ::: isCoupledTo(tree)).distinct.length
    List(new MetricResult(tree.pos, tree.name, "CBO", noc))
  }

  override def run(tree: TraitDefinition, code: List[String]): List[MetricResult] = {
    val noc = (getCoupledModules(tree) ::: isCoupledTo(tree)).distinct.length
    List(new MetricResult(tree.pos, tree.name, "CBO", noc))
  }

  def isCoupledTo(tree: AST): List[String] = tree match {
    case (_: Val | _: Var) =>
      tree.children.foldLeft(List[String]())(_ ::: isCoupledTo(_))
    case x: FunctionCall =>
      x.owner :: x.children.foldLeft(List[String]())(_ ::: isCoupledTo(_))
    case x: Value =>
      x.owner :: x.children.foldLeft(List[String]())(_ ::: isCoupledTo(_))
    case _ =>
      tree.children.foldLeft(List[String]())(_ ::: isCoupledTo(_))
  }

  def getCoupledModules(tree: Module): List[String] = {
    def recursive(ast: AST) : List[String] = ast match {
      case x: Module =>
        if (isCoupled(x, tree.pack  + tree.name))
          x.pack + x.name :: x.children.foldLeft(List[String]())(_ ::: recursive(_))
        else
          x.children.foldLeft(List[String]())(_ ::: recursive(_))
      case _ =>
        ast.children.foldLeft(List[String]())(_ ::: recursive(_))
    }


    getFilesOccurrence(projectContext.getFiles, tree.name).foldLeft(List[String]()){
      (a, b) =>
        val c = if (projectContext.isCached(b)) projectContext.getCached(b).get else projectContext.compiler.treeFromFile(b)
        if (c == null) {
          a
        }else {
          projectContext.addFileToCache(b)
          projectContext.addPreCompiledFile(b, c)
          a ::: recursive(c)
        }
    }
  }

  def isCoupled(tree: AST, origin: String) : Boolean = tree match {
    case x: FunctionCall =>
      if(x.owner == origin)
        true
      else
        tree.children.exists(y => isCoupled(y, origin))
    case x: Value =>
      if(x.owner == origin)
        true
      else
        tree.children.exists(y => isCoupled(y, origin))
    case _ =>
      tree.children.exists(y => isCoupled(y, origin))


  }
}
