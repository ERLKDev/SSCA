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
class MetricRunner(compiler: CompilerS, metrics : List[Metric], context: ProjectContext) extends ProjectUtil with ResultUtil{


  /**
    * Function to run the codeAnalysis.metrics on a single file
    * @param file the file on which the codeAnalysis.metrics should be executed
    * @return the results of the codeAnalysis.metrics on the project
    */
  def run(file: File): ResultUnit ={
    def traverse(tree: AST, parent: ResultUnit) : ResultUnit = tree match {
      case null =>
        tree.children.foreach(x => traverse(x, parent))
        parent

      case node: Module =>
        val result = new ObjectResult(node.pos, node.name, ObjectType.ObjectT)
        result.addResult(executeObjectMetrics(node))
        tree.children.foreach(x => traverse(x, result))

        parent.addResult(result)
        parent

      case node: FunctionDef =>
        val result = new FunctionResult(node.pos, node.name)
        result.addResult(executeFunctionMetrics(node))
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
    val cachedFile = if (context.isCached(file)) context.getCached(file).get else compiler.treeFromFile(file)
    if (cachedFile == null) {
      null
    }else {
      context.addPreCompiledFile(file, cachedFile)
      traverse(cachedFile, null)
    }
  }


  /**
    * Function to run the codeAnalysis.metrics on the file list.
    * @param metrics a list with the codeAnalysis.metrics
    * @param files a list of files
    * @return the results of the codeAnalysis.metrics on the list of files
    */
  def runFiles(metrics: List[Metric], files: List[File]): List[ResultUnit] = {
    files.map(x => run(x)).filter(_ != null)
  }


  /**
    * Function that is called when a module is found
    * This function runs the module metrics
    *
    * @param tree
    * @return
    */
  private def executeObjectMetrics(tree: AST): List[MetricResult] ={
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

  /**
    * Function that is called when a function is found
    * This function runs the function metrics
    * @param tree
    * @return
    */
  private def executeFunctionMetrics(tree: FunctionDef): List[MetricResult] = {
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
