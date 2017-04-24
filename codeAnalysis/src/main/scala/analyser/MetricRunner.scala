package main.scala.analyser

import java.io.File

import analyser.Compiler.{CompilerS, TreeWrapper}
import analyser.result._
import main.scala.analyser.context.ProjectContext
import main.scala.analyser.metric.{FunctionMetric, Metric, ObjectMetric}
import main.scala.analyser.result._
import main.scala.analyser.util.{TreeSyntaxUtil, TreeUtil}

/**
  * Created by Erik on 5-4-2017.
  */
class MetricRunner(compiler: CompilerS) extends TreeUtil with TreeSyntaxUtil{
  import compiler.global._

  /**
    * Function to run the metrics on a single file
    * @param metrics a list with the metrics
    * @param file the file on which the metrics should be executed
    * @param projectContext the project context
    * @return the results of the metrics on the project
    */
  def run(metrics : List[Metric], file: File, projectContext: ProjectContext): ResultUnit ={
    def traverse(tree: TreeWrapper, parent: ResultUnit) : ResultUnit = getAstNode(tree) match {
      case null =>
        tree.unWrap().children.foreach(x => traverse(new TreeWrapper(compiler).wrap(x), parent))
        parent

      case _: ObjectDefinition =>
        val result = new ObjectResult(getRangePos(tree), getName(tree), ObjectType.ObjectT)
        result.addResult(executeObjectMetrics(metrics, tree))
        tree.unWrap().children.foreach(x => traverse(new TreeWrapper(compiler).wrap(x), parent))

        parent.addResult(result)
        parent

      case (_: ClassDefinition) | (_: TraitDefinition) | (_: AbstractClassDefinition)=>
        val result = new ObjectResult(getRangePos(tree), getName(tree), if (isTrait(tree)) ObjectType.TraitT else ObjectType.ClassT)
        result.addResult(executeObjectMetrics(metrics, tree))
        tree.unWrap().children.foreach(x => traverse(new TreeWrapper(compiler).wrap(x), parent))

        parent.addResult(result)
        parent

      case _: FunctionDef =>
        val result = new FunctionResult(getRangePos(tree), getName(tree))
        result.addResult(executeFunctionMetrics(metrics, tree))
        tree.unWrap().children.foreach(x => traverse(new TreeWrapper(compiler).wrap(x), parent))

        parent.addResult(result)
        parent

      case _: PackageDefinition =>
        val result = new FileResult(getRangePos(tree), tree.unWrap().pos.source.path)
        tree.unWrap().children.foreach(x => traverse(new TreeWrapper(compiler).wrap(x), parent))
        result

      case _ =>
        tree.unWrap().children.foreach(x => traverse(new TreeWrapper(compiler).wrap(x), parent))
        parent
    }

    /* Start traversal*/
    val ast = compiler.treeFromFile(file)
    ask{
      () => traverse(ast, null)
    }
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
  private def executeObjectMetrics(metrics : List[Metric], wrappedTree: TreeWrapper): List[MetricResult] ={
    val code = getOriginalSourceCode(wrappedTree)
    if (code == null)
      return List[MetricResult]()

    metrics.foldLeft(List[MetricResult]()){
      (a, b) =>
        b match {
          case x: ObjectMetric =>
            a ::: x.run(wrappedTree, code)
          case _ =>
            a
        }
    }
  }

  /* Function that is called to execute the function metrics. */
  private def executeFunctionMetrics(metrics : List[Metric], wrappedTree: TreeWrapper): List[MetricResult] = {
    val code = getOriginalSourceCode(wrappedTree)
    if (code == null)
      return List[MetricResult]()

    metrics.foldLeft(List[MetricResult]()) {
      (a, b) =>
        b match {
          case x: FunctionMetric =>
            a ::: x.run(wrappedTree, code)
          case _ =>
            a
        }
    }
  }
}
