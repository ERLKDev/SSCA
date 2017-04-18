package main.scala.analyser

import java.io.File

import main.scala.analyser.Compiler.CompilerProvider
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.{FunctionMetric, Metric, ObjectMetric}
import main.scala.analyser.result._
import main.scala.analyser.util.{TreeSyntaxUtil, TreeUtil}

/**
  * Created by Erik on 5-4-2017.
  */
class MetricRunner extends CompilerProvider with TreeUtil with TreeSyntaxUtil{
  import global._

  def run(metrics : List[Metric], file: File, projectContext: ProjectContext): Result ={
    def traverse(tree: Tree) : Result = getAstNode(tree) match {
      case null =>
        tree.children.foldLeft(new ResultList())((a, b) => a.add(traverse(b)))

      case ObjectDefinition(x, _, _) =>
        UnitResult(getRangePos(x), UnitType.Object, getName(x), traverse(x.impl) :: executeObjectMetrics(metrics, x))

      case (_: ClassDefinition) | (_: TraitDefinition) | (_: AbstractClassDefinition)=>
        val x = tree.asInstanceOf[ClassDef]
        UnitResult(getRangePos(x), UnitType.Object, getName(x), traverse(x.impl) :: executeObjectMetrics(metrics, x))

      case FunctionDef(x, _, _) =>
        UnitResult(getRangePos(x), UnitType.Function, getName(x), traverse(x.tpt) :: traverse(x.rhs) :: executeFunctionMetrics(metrics, x))

      case PackageDefinition(x) =>
        UnitResult(getRangePos(x), UnitType.File, x.pos.source.path, x.children.foldLeft(new ResultList())((a, b) => a.add(traverse(b))).getList)

      case _ =>
        tree.children.foldLeft(new ResultList())((a, b) => a.add(traverse(b)))
    }

    /* Init main.scala.metrics*/
    metrics.foreach(f => f.init(projectContext))

    /* Start traversal*/
    traverse(treeFromFile(file))
  }

  def runProject(metrics: List[Metric], files: List[File], projectContext: ProjectContext): Result = {
    UnitResult(null, UnitType.Project, "project", files.foldLeft(List[Result]())((a, b) =>  a ::: List(run(metrics, b, projectContext))))
  }


  private def executeObjectMetrics(metrics : List[Metric], tree: Tree): List[MetricResult] ={
    val code = getOriginalSourceCode(tree)
    if (code == null)
      return List[MetricResult]()

    metrics.foldLeft(List[MetricResult]()){
      (a, b) =>
        b match {
          case x: ObjectMetric =>
            tree match {
              case y: ModuleDef =>
                a ::: x.run(y.asInstanceOf[x.global.ModuleDef], code)
              case y: ClassDef =>
                a ::: x.run(y.asInstanceOf[x.global.ClassDef], code)
            }
          case _ =>
            a
        }
    }
  }


  private def executeFunctionMetrics(metrics : List[Metric], tree: Tree): List[MetricResult] = {
    val code = getOriginalSourceCode(tree)
    if (code == null)
      return List[MetricResult]()

    metrics.foldLeft(List[MetricResult]()) {
      (a, b) =>
        b match {
          case x: FunctionMetric =>
            a ::: x.run(tree.asInstanceOf[x.global.DefDef], code)
          case _ =>
            a
        }
    }
  }
}
