package main.scala.analyser

import java.io.File

import analyser.result._
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


  /**
    * Function to run the metrics on a single file
    * @param metrics a list with the metrics
    * @param file the file on which the metrics should be executed
    * @param projectContext the project context
    * @return the results of the metrics on the project
    */
  def run(metrics : List[Metric], file: File, projectContext: ProjectContext): ResultUnit ={
    def traverse(tree: Tree, parent: ResultUnit) : ResultUnit = getAstNode(tree) match {
      case null =>
        tree.children.foreach(x => traverse(x, parent))
        parent

      case ObjectDefinition(x, _, _) =>
        val result = new ObjectResult(getRangePos(x), getName(x), ObjectType.ObjectT)
        result.addResult(executeObjectMetrics(metrics, x))
        traverse(x.impl, result)

        parent.addResult(result)
        parent

      case (_: ClassDefinition) | (_: TraitDefinition) | (_: AbstractClassDefinition)=>
        val x = tree.asInstanceOf[ClassDef]
        val result = new ObjectResult(getRangePos(x), getName(x), if (isTrait(x)) ObjectType.TraitT else ObjectType.ClassT)
        result.addResult(executeObjectMetrics(metrics, x))
        traverse(x.impl, result)

        parent.addResult(result)
        parent

      case FunctionDef(x, _, _) =>
        val result = new FunctionResult(getRangePos(x), getName(x))
        result.addResult(executeFunctionMetrics(metrics, x))
        traverse(x.tpt, result)
        traverse(x.rhs, result)

        parent.addResult(result)
        parent

      case PackageDefinition(x) =>
        val result = new FileResult(getRangePos(x), x.pos.source.path)
        x.children.foreach(y => traverse(y, result))
        result

      case _ =>
        tree.children.foreach(x => traverse(x, parent))
        parent
    }

    /* Start traversal*/
    traverse(treeFromFile(file), null)
  }

  /**
    * Function to run the metrics on the file list.
    * @param metrics a list with the metrics
    * @param files a list of files
    * @param projectContext the project context
    * @return the results of the metrics on the list of files
    */
  def runFiles(metrics: List[Metric], files: List[File], projectContext: ProjectContext): List[ResultUnit] = {
    files.foldLeft(List[ResultUnit]())((a, b) =>  a ::: List(run(metrics, b, projectContext)))
  }

  /**
    * Function to run the metrics on the entire project.
    * @param metrics a list with the metrics
    * @param files a list of the project files
    * @param projectContext the project context
    * @return the results of the metrics on the project
    */
  def runProject(metrics: List[Metric], files: List[File], projectContext: ProjectContext): List[ResultUnit] = {
    files.foldLeft(List[ResultUnit]())((a, b) =>  a ::: List(run(metrics, b, projectContext)))
  }


  /* Function that is called to execute the object metrics. */
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

  /* Function that is called to execute the function metrics. */
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
