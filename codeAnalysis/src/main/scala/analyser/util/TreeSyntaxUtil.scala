package main.scala.analyser.util

import analyser.Compiler.CompilerS
import analyser.AST._

/**
  * Created by Erik on 14-4-2017.
  */
class TreeSyntaxUtil(override val compiler: CompilerS) extends TreeUtil(compiler) {
  import compiler.global._



  def parseTree(tree: Tree): AST = {
    compiler.global.ask{
      () =>
        val ast = getAstNode(tree)
        if (ast == null)
          new AST(getChildren(tree), getRangePos(tree))
        else
          ast
    }
  }


  /**
    * Adds a wrapper to a tree
    * This makes the node easy to use
    *
    * @param tree the tree
    * @return
    */
  private def getAstNode(tree: Tree): AST = {
    try tree match {
      case x: PackageDef =>
        if (isPackage(x)) {
          return PackageDefinition(getChildren(x), getRangePos(tree))
        }
        null

      case x: ClassDef =>
        if (isTrait(x))
          return TraitDefinition(getChildren(x), getRangePos(tree), getParents(x.symbol.parentSymbols), getName(x), getObjectPackage(x.symbol))
        if (isAbstractClass(x))
          return ClassDefinition(getChildren(x), getRangePos(tree), getParents(x.symbol.parentSymbols),getName(x), getObjectPackage(x.symbol), true)
        if (isClass(x))
          return ClassDefinition(getChildren(x), getRangePos(tree), getParents(x.symbol.parentSymbols),getName(x), getObjectPackage(x.symbol), false)
        if (isAnonymousClass(x))
          return AnonymousClass(getChildren(x), getRangePos(tree), getParents(x.symbol.parentSymbols))
        null

      case x: ModuleDef =>
        if (isObject(x))
          return ObjectDefinition(getChildren(x), getRangePos(tree), getParents(x.symbol.parentSymbols),getName(x), getObjectPackage(x.symbol))
        null

      case x: DefDef =>
        if (isAnonymousFunction(x))
          return AnonymousFunction(getChildren(x), getRangePos(tree))
        if (isNestedFunction(x))
          return NestedFunction(getChildren(x), getRangePos(tree), getName(x), getOwner(x.symbol.owner))
        if (isFunction(x))
          return FunctionDef(getChildren(x), getRangePos(tree), getName(x), getOwner(x.symbol.owner))
        null

      case x: ValDef =>
        if (isValDef(x))
          return ValDefinition(getChildren(x), getRangePos(tree), x.name.toString)
        if (isVarDef(x))
          return VarDefinition(getChildren(x), getRangePos(tree), x.name.toString)
        null

      case x: Ident =>
        if (isVal(x))
          return Val(getChildren(x), getRangePos(tree), x.name.toString)
        if (isVar(x))
          return Var(getChildren(x), getRangePos(tree), x.name.toString)
        null

      case x: Assign =>
        if (isAssignment(x) && isVal(x.lhs))
          return ValAssignment(getChildren(x), getRangePos(tree), x.lhs.symbol.name.toString)
        if (isAssignment(x) && isVar(x.lhs))
          return VarAssignment(getChildren(x), getRangePos(tree), x.lhs.symbol.name.toString)
        null

      case x: Match =>
        if (isMatch(x))
          return MatchCase(getChildren(x), getRangePos(tree))
        null

      case x: CaseDef =>
        if (isCase(x))
          return Case(getChildren(x), getRangePos(tree))
        null

      case x: Apply =>
        if (isFor(x))
          return For(getChildren(x), getRangePos(tree))
        if (isFunctionCall(x)) {
          val a = FunctionCall(getChildren(x), getRangePos(tree), x.fun.symbol.name.toString, getOwner(x.fun.symbol.owner))
          return a
        }
        null

      case x: LabelDef =>
        if (isWhile(x))
          return While(getChildren(x), getRangePos(tree))
        if (isDoWhile(x))
          return DoWhile(getChildren(x), getRangePos(tree))
        null

      case x: New =>
        if (isNewClass(x))
          return NewClass(getChildren(x), getRangePos(tree), x.tpt.symbol.name.toString)
        null

      case x: If =>
        if (isIf(x))
          return IfStatement(getChildren(x), getRangePos(tree))
        null

      case x =>
        null
    }catch{
      case x: Throwable =>
        null
    }
  }

  private def getChildren(tree: Tree): List[AST] = {
    tree.children.foldLeft(List[AST]()){
      (a, b) =>
        val child = getAstNode(b)
        if (child == null)
          a ::: getChildren(b)
        else
          a ::: List(child)
    }
  }

  /**
    * Check's if the tree is a packages node
    * @param tree the ast
    * @return
    */
  def isPackage(tree: Tree): Boolean = tree match {
    case x: PackageDef =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is a trait
    * @param tree the ast
    * @return
    */
  def isTrait(tree: Tree): Boolean = tree match {
    case x: ClassDef =>
      x.mods.isTrait
    case _ =>
      false
  }

  /**
    * Check's if the tree is a class
    * @param tree the ast
    * @return
    */
  def isClass(tree: Tree): Boolean = tree match  {
    case x: ClassDef =>
      !x.mods.isTrait
    case _ =>
      false
  }

  /**
    * Check's if the tree is a object
    * @param tree the ast
    * @return
    */
  def isObject(tree: Tree): Boolean = tree match {
    case _: ModuleDef =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is an abstract class
    * @param tree the ast
    * @return
    */
  def isAbstractClass(tree: Tree): Boolean = tree match  {
    case x: ClassDef =>
      x.symbol.isAbstractClass
    case _ =>
      false
  }

  /**
    * Check's if the tree is an anonymous class
    * @param tree the ast
    * @return
    */
  def isAnonymousClass(tree: Tree): Boolean = tree match  {
    case x: ClassDef =>
      isClass(x) && x.symbol.isAnonymousClass
    case _ =>
      false
  }

  /**
    * Check's if the tree is a function
    * @param tree the ast
    * @return
    */
  def isFunction(tree: Tree): Boolean = tree match {
    case _: DefDef =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is an anonymous function
    * @param tree the ast
    * @return
    */
  def isAnonymousFunction(tree: Tree): Boolean = tree match {
    case x: Function =>
      x.symbol.isAnonymousFunction
    case _ =>
      false
  }

  /**
    * Check's if the tree is a nested function
    * @param tree the ast
    * @return
    */
  def isNestedFunction(tree: Tree): Boolean = tree match {
    case x:DefDef =>
      x.symbol.owner.isMethod
    case _ =>
      false
  }

  /**
    * Check's if the tree is a function call
    * @param tree the ast
    * @return
    */
  def isFunctionCall(tree: Tree): Boolean = tree match {
    case x: Apply =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is an assignment
    * @param tree the ast
    * @return
    */
  def isAssignment(tree: Tree): Boolean = tree match  {
    case _: Assign =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is a new statement
    * @param tree the ast
    * @return
    */
  def isNewClass(tree: Tree): Boolean = tree match  {
    case _: New =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is a val definition
    * @param tree the ast
    * @return
    */
  def isValDef(tree: Tree): Boolean = tree match  {
    case x: ValDef =>
      !x.symbol.isMutable
    case _ =>
      false
  }

  /**
    * Check's if the tree is a var definition
    * @param tree the ast
    * @return
    */
  def isVarDef(tree: Tree): Boolean = tree match {
    case x: ValDef =>
      x.symbol.isMutable
    case _ =>
      false
  }

  /**
    * Check's if the tree is a var
    * @param tree the ast
    * @return
    */
  def isVar(tree: Tree): Boolean = tree match {
    case x: Ident =>
      x.symbol.isVar
    case _ =>
      false
  }

  /**
    * Check's if the tree is a val
    * @param tree the ast
    * @return
    */
  def isVal(tree: Tree): Boolean = tree match {
    case x: Ident =>
      x.symbol.isVal
    case _ =>
      false
  }

  /**
    * Check's if the tree is for statement
    * @param tree the ast
    * @return
    */
  def isFor(tree: Tree): Boolean = {
    isCall(tree, "foreach")
  }

  /**
    * Check's if the tree is a while statement
    * @param tree the ast
    * @return
    */
  def isWhile(tree: Tree): Boolean = tree match {
    case x:LabelDef =>
      ("""^while\$(\d)*""".r findFirstIn x.name.toString).nonEmpty
    case _ =>
      false
  }

  /**
    * Check's if the tree is a do while
    * @param tree the ast
    * @return
    */
  def isDoWhile(tree: Tree): Boolean = tree match {
    case x:LabelDef =>
      ("""^doWhile\$(\d)*""".r findFirstIn x.name.toString).nonEmpty
    case _ =>
      false
  }

  /**
    * Check's if the tree is a function call to a specific function
    * @param tree the ast
    * @return
    */
  def isCall(tree: Tree, name: String): Boolean = tree match {
    case x: Apply =>
      x.fun.symbol.name.toString == name
    case _ =>
      false
  }

  /**
    * Check's if the tree is a match statement
    * @param tree the ast
    * @return
    */
  def isMatch(tree : Tree): Boolean = tree match {
    case _:Match =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is a case statement
    * @param tree the ast
    * @return
    */
  def isCase(tree : Tree): Boolean = tree match {
    case _:CaseDef =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is an if statement
    * @param tree the ast
    * @return
    */
  def isIf(tree : Tree): Boolean = tree match {
    case _:If =>
      true
    case _ =>
      false
  }
}
