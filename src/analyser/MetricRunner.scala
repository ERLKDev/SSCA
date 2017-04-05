package analyser

import analyser.Compiler.CompilerProvider
import analyser.metric.{FunctionMetric, Metric, ObjectMetric}
import analyser.result.{MetricResult, Result}
import analyser.util.TreeUtil

import scala.collection.mutable.ListBuffer

/**
  * Created by Erik on 5-4-2017.
  */
class MetricRunner extends CompilerProvider with TreeUtil{
  import global._

  def run(metrics : List[Metric], tree: Tree, projectTree : Array[global.Tree]): List[MetricResult] ={
    def traverse(tree: Tree) : List[MetricResult] = tree match {
      case ModuleDef(_, _, content : Tree) =>
        traverse(content) ++ executeObjectMetrics(metrics, tree)
      case ClassDef(_, _, _, impl) =>
        traverse(impl) ++ executeObjectMetrics(metrics, tree)
      case DefDef(_, _, _, _, tpt, rhs) =>
        traverse(tpt) ++ traverse(rhs) ++ executeFunctionMetrics(metrics, tree)
      case _ =>
        val c = tree.children.foldLeft(List[MetricResult]())((a, b) => a ++ traverse(b))
        c
    }

    def executeObjectMetrics(metrics : List[Metric], tree: Tree): List[MetricResult] ={
      val code = getOriginalSourceCode(tree)
      if (code == null)
        return List[MetricResult]()

      val results = new ListBuffer[MetricResult]
      metrics.foreach {
        case x: ObjectMetric =>
          tree match {
            case tree: ModuleDef =>
              results ++= x.run(tree.asInstanceOf[x.global.ModuleDef], code, projectTree.asInstanceOf[Array[x.global.Tree]], null)
            case tree: ClassDef =>
              results ++= x.run(tree.asInstanceOf[x.global.ClassDef], code, projectTree.asInstanceOf[Array[x.global.Tree]], null)
          }
        case _ =>

      }
      results.toList
    }


    def executeFunctionMetrics(metrics : List[Metric], tree: Tree): List[MetricResult] ={
      val code = getOriginalSourceCode(tree)
      if (code == null)
        return List[MetricResult]()

      val results = new ListBuffer[MetricResult]
      metrics.foreach {
        case x: FunctionMetric =>
          results ++= x.run(tree.asInstanceOf[x.global.DefDef], code, projectTree.asInstanceOf[Array[x.global.Tree]], null)
        case _ =>

      }
      results.toList
    }

    traverse(tree)
  }

}
