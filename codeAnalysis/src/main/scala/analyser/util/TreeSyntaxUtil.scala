package main.scala.analyser.util

import main.scala.analyser.Compiler.CompilerProvider

/**
  * Created by Erik on 14-4-2017.
  */
trait TreeSyntaxUtil extends CompilerProvider with TreeUtil{
  import global._

  /**
    * Case classes for the AST node wrappers
    */
  trait AstNode
  case class PackageDefinition(tree: PackageDef) extends AstNode
  case class TraitDefinition(tree: ClassDef, name: String, pack: String) extends AstNode
  case class ClassDefinition(tree: ClassDef, name: String, pack: String) extends AstNode
  case class AbstractClassDefinition(tree: ClassDef, name: String, pack: String) extends AstNode
  case class ObjectDefinition(tree: ModuleDef, name: String, pack: String) extends AstNode
  case class AnonymousClass(tree: ClassDef) extends AstNode
  case class FunctionDef(tree: DefDef, name: String, owner: String) extends AstNode
  case class AnonymousFunction(tree: DefDef) extends AstNode
  case class NestedFunction(tree: DefDef, name: String, owner: String) extends AstNode
  case class FunctionCall(tree: Apply, name: String, owner: String) extends AstNode
  case class ValAssignment(tree: Assign, variable: String) extends AstNode
  case class VarAssignment(tree: Assign, variable: String) extends AstNode
  case class NewClass(tree: New, name: String) extends AstNode
  case class ValDefinition(tree: ValDef, name: String) extends AstNode
  case class VarDefinition(tree: ValDef, name: String) extends AstNode
  case class Var(tree: Ident, name: String) extends AstNode
  case class Val(tree: Ident, name: String) extends AstNode
  case class For(tree: Apply) extends AstNode
  case class While(tree: LabelDef) extends AstNode
  case class DoWhile(tree: LabelDef) extends AstNode
  case class MatchCase(tree: Match) extends AstNode
  case class Case(tree: CaseDef) extends AstNode
  case class IfStatement(tree: If) extends AstNode


  /**
    * Adds a wrapper to a tree
    * This makes the node easy to use
    *
    * @param tree the tree
    * @return
    */
  def getAstNode(tree: Tree): AstNode = {
    try tree match {
      case x: PackageDef =>
        if (isPackage(x))
          return PackageDefinition(x)
        null

      case x: ClassDef =>
        if (isTrait(x))
          return TraitDefinition(x, getName(x), getObjectPackage(x.symbol))
        if (isAbstractClass(x))
          return AbstractClassDefinition(x, getName(x), getObjectPackage(x.symbol))
        if (isClass(x))
          return ClassDefinition(x, getName(x), getObjectPackage(x.symbol))
        if (isAnonymousClass(x))
          return AnonymousClass(x)
        null

      case x: ModuleDef =>
        if (isObject(x))
          return ObjectDefinition(x, getName(x), getObjectPackage(x.symbol))
        null

      case x: DefDef =>
        if (isAnonymousFunction(x))
          return AnonymousFunction(x)
        if (isNestedFunction(x))
          return NestedFunction(x, getName(x), getOwner(x.symbol.owner))
        if (isFunction(x))
          return FunctionDef(x, getName(x), getOwner(x.symbol.owner))
        null

      case x: ValDef =>
        if (isValDef(x))
          return ValDefinition(x, x.name.toString)
        if (isVarDef(x))
          return VarDefinition(x, x.name.toString)
        null

      case x: Ident =>
        if (isVal(x))
          return Val(x, x.name.toString)
        if (isVar(x))
          return Var(x, x.name.toString)
        null

      case x: Assign =>
        if (isAssignment(x) && isVal(x.lhs))
          return ValAssignment(x, x.lhs.symbol.name.toString)
        if (isAssignment(x) && isVar(x.lhs))
          return VarAssignment(x, x.lhs.symbol.name.toString)
        null

      case x: Match =>
        if (isMatch(x))
          return MatchCase(x)
        null

      case x: CaseDef =>
        if (isCase(x))
          return Case(x)
        null

      case x: Apply =>
        if (isFor(x))
          return For(x)
        if (isFunctionCall(x)) {
          val a = FunctionCall(x, x.fun.symbol.name.toString, getOwner(x.fun.symbol.owner))
          return a
        }
        null

      case x: LabelDef =>
        if (isWhile(x))
          return While(x)
        if (isDoWhile(x))
          return DoWhile(x)
        null

      case x: New =>
        if (isNewClass(x))
          return NewClass(x, x.tpt.symbol.name.toString)
        null

      case x: If =>
        if (isIf(x))
          return IfStatement(x)
        null

      case _ =>
        null
    }catch{
      case _: Throwable =>
        null
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
