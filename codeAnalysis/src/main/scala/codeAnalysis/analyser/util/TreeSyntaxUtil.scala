package main.scala.analyser.util

import codeAnalysis.analyser.Compiler.CompilerS
import codeAnalysis.analyser.AST._

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
        PackageDefinition(getChildren(x), getRangePos(tree))

      case x: ClassDef =>
        if (isTrait(x))
          TraitDefinition(getChildren(x), getRangePos(tree), getParents(x.symbol.baseClasses), getName(x), getObjectPackage(x.symbol))
        else
          ClassDefinition(getChildren(x), getRangePos(tree), getParents(x.symbol.baseClasses), getName(x), getObjectPackage(x.symbol), isAbstractClass(x), isNested(x), isAnonymousClass(x))

      case x: ModuleDef =>
        ObjectDefinition(getChildren(x), getRangePos(tree), getParents(x.symbol.baseClasses),getName(x), getObjectPackage(x.symbol), isNested(x))

      case x: DefDef =>
        FunctionDef(getChildren(x), getRangePos(tree), getName(x), getOwner(x.symbol.owner), isNested(x), isAnonymousFunction(x))

      case x: ValDef =>
        if (isValDef(x))
          ValDefinition(getChildren(x), getRangePos(tree), x.name.toString)
        else if (isVarDef(x))
          VarDefinition(getChildren(x), getRangePos(tree), x.name.toString)
        else
          null

      case x: Ident =>
        if (isVal(x))
          Val(getChildren(x), getRangePos(tree), x.name.toString)
        else if (isVar(x))
          Var(getChildren(x), getRangePos(tree), x.name.toString)
        else
          null

      case x: Assign =>
        if (isAssignment(x) && isVal(x.lhs))
          ValAssignment(getChildren(x), getRangePos(tree), x.lhs.symbol.name.toString)
        else if (isAssignment(x) && isVar(x.lhs))
          VarAssignment(getChildren(x), getRangePos(tree), x.lhs.symbol.name.toString)
        else
          null

      case x: Match =>
        MatchCase(getChildren(x), getRangePos(tree))

      case x: CaseDef =>
        Case(getChildren(x), getRangePos(tree))

      case x: Apply =>
        if (isFor(x))
          For(getChildren(x), getRangePos(tree))
        else if (isFunctionCall(x))
          FunctionCall(getChildren(x), getRangePos(tree), x.fun.symbol.name.toString, getOwner(x.fun.symbol.owner))
        else
          null

      case x: LabelDef =>
        if (isWhile(x))
          While(getChildren(x), getRangePos(tree))
        else if (isDoWhile(x))
          DoWhile(getChildren(x), getRangePos(tree))
        else
          null

      case x: New =>
        NewClass(getChildren(x), getRangePos(tree), x.tpt.symbol.name.toString)

      case x: If =>
        IfStatement(getChildren(x), getRangePos(tree))

      case x =>
        null
    }catch{
      case x: Throwable =>
        error("Error parsing")
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
  def isNested(tree: Tree): Boolean = tree match {
    case x:DefDef =>
      x.symbol.owner.isMethod
    case x:ClassDef =>
      x.symbol.isNestedClass || x.symbol.owner.isTrait || x.symbol.owner.isClass || x.symbol.owner.isMethod || x.symbol.isModuleOrModuleClass
    case x:ModuleDef =>
      x.symbol.isNestedClass || x.symbol.owner.isTrait || x.symbol.owner.isClass || x.symbol.owner.isMethod || x.symbol.isModuleOrModuleClass
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
