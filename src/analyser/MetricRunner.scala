package analyser

import analyser.Compiler.CompilerProvider
import analyser.metric.{FunctionMetric, Metric, ObjectMetric}
import analyser.result._
import analyser.util.TreeUtil

import scala.collection.mutable.ListBuffer

/**
  * Created by Erik on 5-4-2017.
  */
class MetricRunner extends CompilerProvider with TreeUtil{
  import global._

  def run(metrics : List[Metric], tree: Array[Tree], projectTree : Array[global.Tree]): Result ={
    def traverse(tree: Tree) : Result = tree match {
      case ModuleDef(_, _, content : Tree) =>
        new UnitResult(getRangePos(tree), UnitType.Object, getName(tree.asInstanceOf[ModuleDef]), traverse(content) :: executeObjectMetrics(metrics, tree))
      case ClassDef(_, _, _, impl) =>
        new UnitResult(getRangePos(tree), UnitType.Object, getName(tree.asInstanceOf[ClassDef]), traverse(impl) :: executeObjectMetrics(metrics, tree))
      case DefDef(_, _, _, _, tpt, rhs) =>
        new UnitResult(getRangePos(tree), UnitType.Function, getName(tree.asInstanceOf[DefDef]), traverse(tpt) :: traverse(rhs) :: executeFunctionMetrics(metrics, tree))
      case x: PackageDef =>
        new UnitResult(getRangePos(tree), UnitType.File, x.pos.source.path, tree.children.foldLeft(new ResultList())((a, b) => a.add(traverse(b))).getList)
      case _ =>
        tree.children.foldLeft(new ResultList())((a, b) => a.add(traverse(b)))
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
              results ++= x.run(tree.asInstanceOf[x.global.ModuleDef], code)
            case tree: ClassDef =>
              results ++= x.run(tree.asInstanceOf[x.global.ClassDef], code)
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
          results ++= x.run(tree.asInstanceOf[x.global.DefDef], code)
        case _ =>

      }
      results.toList
    }

    /* Init metrics*/
    metrics.foreach(f => f.init(projectTree.asInstanceOf[Array[f.global.Tree]], getOriginalSourceCode(projectTree)))

    /* Start traversal*/
    new UnitResult(null, UnitType.Project, "project", tree.foldLeft(List[Result]())((a, b) =>  a ::: List(traverse(b))))
  }
}
