package main.scala.analyser

import java.io.File

import codeAnalysis.analyser.Compiler.CompilerS
import codeAnalysis.analyser.result._
import codeAnalysis.analyser.AST._
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.{FunctionMetric, Metric, ObjectMetric}
import main.scala.analyser.result._
import main.scala.analyser.util.{ProjectUtil, ResultUtil}

/**
  * Created by Erik on 5-4-2017.
  */
class MetricRunner(compiler: CompilerS) extends ProjectUtil with ResultUtil{


  /**
    * Function to run the codeAnalysis.metrics on a single file
    * @param metrics a list with the codeAnalysis.metrics
    * @param file the file on which the codeAnalysis.metrics should be executed
    * @param projectContext the project context
    * @return the results of the codeAnalysis.metrics on the project
    */
  def run(metrics : List[Metric], file: File, projectContext: ProjectContext): ResultUnit ={
    def traverse(tree: AST, parent: ResultUnit) : ResultUnit = tree match {
      case null =>
        tree.children.foreach(x => traverse(x, parent))
        parent

      case node: ObjectDefinition =>
        val result = new ObjectResult(node.pos, node.name, ObjectType.ObjectT)
        result.addResult(executeObjectMetrics(metrics, node))
        tree.children.foreach(x => traverse(x, result))

        parent.addResult(result)
        parent

      case node: ClassDefinition=>
        val result = new ObjectResult(node.pos, node.name, ObjectType.ClassT)
        result.addResult(executeObjectMetrics(metrics, node))
        tree.children.foreach(x => traverse(x, result))

        parent.addResult(result)
        parent

      case node: TraitDefinition=>
        val result = new ObjectResult(node.pos, node.name, ObjectType.TraitT)
        result.addResult(executeObjectMetrics(metrics, node))
        tree.children.foreach(x => traverse(x, result))

        parent.addResult(result)
        parent

      case node: FunctionDef =>
        val result = new FunctionResult(node.pos, node.name)
        result.addResult(executeFunctionMetrics(metrics, node))
        tree.children.foreach(x => traverse(x, result))

        parent.addResult(result)
        parent

      case node: PackageDefinition =>
        val result = new FileResult(node.pos, node.pos.source.path)
        tree.children.foreach(x => traverse(x, result))
        result

      case _ =>
        tree.children.foreach(x => traverse(x, parent))
        parent
    }

    /* Start traversal*/
    val a = compiler.treeFromFile(file)
    if (a == null) {
      null
    }else {
      traverse(a, null)
    }
  }


  /**
    * Function to run the codeAnalysis.metrics on the file list.
    * @param metrics a list with the codeAnalysis.metrics
    * @param files a list of files
    * @param projectContext the project context
    * @return the results of the codeAnalysis.metrics on the list of files
    */
  def runFiles(metrics: List[Metric], files: List[File], projectContext: ProjectContext): List[ResultUnit] = {
    files.foldLeft(List[ResultUnit]())((a, b) =>  a ::: List(run(metrics, b, projectContext))).filter(_ != null)
  }


  /* Function that is called to execute the object codeAnalysis.metrics. */
  private def executeObjectMetrics(metrics : List[Metric], tree: AST): List[MetricResult] ={
    val code = getOriginalSourceCode(tree)
    if (code == null)
      return List[MetricResult]()

    metrics.foldLeft(List[MetricResult]()){
      (a, b) =>
        b match {
          case x: ObjectMetric =>
            tree match {
              case y: ObjectDefinition =>
                a ::: x.run(y, code)
              case y: ClassDefinition =>
                a ::: x.run(y, code)
              case y: TraitDefinition =>
                a ::: x.run(y, code)
            }
          case _ =>
            a
        }
    }
  }

  /* Function that is called to execute the function codeAnalysis.metrics. */
  private def executeFunctionMetrics(metrics : List[Metric], tree: FunctionDef): List[MetricResult] = {
    val code = getOriginalSourceCode(tree)
    if (code == null)
      return List[MetricResult]()

    metrics.foldLeft(List[MetricResult]()) {
      (a, b) =>
        b match {
          case x: FunctionMetric =>
            a ::: x.run(tree, code)
          case _ =>
            a
        }
    }
  }
}
