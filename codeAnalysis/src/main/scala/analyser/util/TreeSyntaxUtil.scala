package main.scala.analyser.util

import analyser.Compiler.{SymbolWrapper, TreeWrapper}

/**
  * Created by Erik on 14-4-2017.
  */
trait TreeSyntaxUtil extends TreeUtil{



  /**
    * Case classes for the AST node wrappers
    */
  trait AstNode
  case class PackageDefinition(tree: TreeWrapper) extends AstNode
  case class TraitDefinition(tree: TreeWrapper, name: String, pack: String) extends AstNode
  case class ClassDefinition(tree: TreeWrapper, name: String, pack: String) extends AstNode
  case class AbstractClassDefinition(tree: TreeWrapper, name: String, pack: String) extends AstNode
  case class ObjectDefinition(tree: TreeWrapper, name: String, pack: String) extends AstNode
  case class AnonymousClass(tree: TreeWrapper) extends AstNode
  case class FunctionDef(tree: TreeWrapper, name: String, owner: String) extends AstNode
  case class AnonymousFunction(tree: TreeWrapper) extends AstNode
  case class NestedFunction(tree: TreeWrapper, name: String, owner: String) extends AstNode
  case class FunctionCall(tree: TreeWrapper, name: String, owner: String) extends AstNode
  case class ValAssignment(tree: TreeWrapper, variable: String) extends AstNode
  case class VarAssignment(tree: TreeWrapper, variable: String) extends AstNode
  case class NewClass(tree: TreeWrapper, name: String) extends AstNode
  case class ValDefinition(tree: TreeWrapper, name: String) extends AstNode
  case class VarDefinition(tree: TreeWrapper, name: String) extends AstNode
  case class Var(tree: TreeWrapper, name: String) extends AstNode
  case class Val(tree: TreeWrapper, name: String) extends AstNode
  case class For(tree: TreeWrapper) extends AstNode
  case class While(tree: TreeWrapper) extends AstNode
  case class DoWhile(tree: TreeWrapper) extends AstNode
  case class MatchCase(tree: TreeWrapper) extends AstNode
  case class Case(tree: TreeWrapper) extends AstNode
  case class IfStatement(tree: TreeWrapper) extends AstNode


  /**
    * Adds a wrapper to a tree
    * This makes the node easy to use
    *
    * @param wrappedTree the tree
    * @return
    */
  def getAstNode(wrappedTree: TreeWrapper): AstNode = {
    import wrappedTree.compiler.global._
    try wrappedTree.unWrap() match {
      case x: PackageDef =>
        if (isPackage(wrappedTree))
          return PackageDefinition(wrappedTree)
        null

      case x: ClassDef =>
        val wrappedSymbol = new SymbolWrapper(wrappedTree.compiler)
        wrappedSymbol.wrap(x.symbol.asInstanceOf[wrappedSymbol.compiler.global.Symbol])

        if (isTrait(wrappedTree))
          return TraitDefinition(wrappedTree, getName(wrappedTree), getObjectPackage(wrappedSymbol))
        if (isAbstractClass(wrappedTree))
          return AbstractClassDefinition(wrappedTree, getName(wrappedTree), getObjectPackage(wrappedSymbol))
        if (isClass(wrappedTree))
          return ClassDefinition(wrappedTree, getName(wrappedTree), getObjectPackage(wrappedSymbol))
        if (isAnonymousClass(wrappedTree))
          return AnonymousClass(wrappedTree)
        null

      case x: ModuleDef =>
        val wrappedSymbol = new SymbolWrapper(wrappedTree.compiler)
        wrappedSymbol.wrap(x.symbol.asInstanceOf[wrappedSymbol.compiler.global.Symbol])

        if (isObject(wrappedTree))
          return ObjectDefinition(wrappedTree, getName(wrappedTree), getObjectPackage(wrappedSymbol))
        null

      case x: DefDef =>
        val wrappedSymbol = new SymbolWrapper(wrappedTree.compiler)
        wrappedSymbol.wrap(x.symbol.owner.asInstanceOf[wrappedSymbol.compiler.global.Symbol])

        if (isAnonymousFunction(wrappedTree))
          return AnonymousFunction(wrappedTree)
        if (isNestedFunction(wrappedTree))
          return NestedFunction(wrappedTree, getName(wrappedTree), getOwner(wrappedSymbol))
        if (isFunction(wrappedTree))
          return FunctionDef(wrappedTree, getName(wrappedTree), getOwner(wrappedSymbol))
        null

      case x: ValDef =>
        if (isValDef(wrappedTree))
          return ValDefinition(wrappedTree, x.name.toString)
        if (isVarDef(wrappedTree))
          return VarDefinition(wrappedTree, x.name.toString)
        null

      case x: Ident =>
        if (isVal(wrappedTree))
          return Val(wrappedTree, x.name.toString)
        if (isVar(wrappedTree))
          return Var(wrappedTree, x.name.toString)
        null

      case x: Assign =>
        if (isAssignment(wrappedTree) && isVal(new TreeWrapper(wrappedTree.compiler).wrap(x.lhs)))
          return ValAssignment(wrappedTree, x.lhs.symbol.name.toString)
        if (isAssignment(wrappedTree) && isVar(new TreeWrapper(wrappedTree.compiler).wrap(x.lhs)))
          return VarAssignment(wrappedTree, x.lhs.symbol.name.toString)
        null

      case x: Match =>
        if (isMatch(wrappedTree))
          return MatchCase(wrappedTree)
        null

      case x: CaseDef =>
        if (isCase(wrappedTree))
          return Case(wrappedTree)
        null

      case x: Apply =>
        val wrappedSymbol = new SymbolWrapper(wrappedTree.compiler)
        wrappedSymbol.wrap(x.fun.symbol.owner.asInstanceOf[wrappedSymbol.compiler.global.Symbol])

        if (isFor(wrappedTree))
          return For(wrappedTree)
        if (isFunctionCall(wrappedTree)) {
          val a = FunctionCall(wrappedTree, x.fun.symbol.name.toString, getOwner(wrappedSymbol))
          return a
        }
        null

      case x: LabelDef =>
        if (isWhile(wrappedTree))
          return While(wrappedTree)
        if (isDoWhile(wrappedTree))
          return DoWhile(wrappedTree)
        null

      case x: New =>
        if (isNewClass(wrappedTree))
          return NewClass(wrappedTree, x.tpt.symbol.name.toString)
        null

      case x: If =>
        if (isIf(wrappedTree))
          return IfStatement(wrappedTree)
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
    * @param wrappedTree the ast
    * @return
    */
  def isPackage(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.PackageDef =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is a trait
    * @param wrappedTree the ast
    * @return
    */
  def isTrait(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.ClassDef =>
      x.mods.isTrait
    case _ =>
      false
  }

  /**
    * Check's if the tree is a class
    * @param wrappedTree the ast
    * @return
    */
  def isClass(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match  {
    case x: wrappedTree.compiler.global.ClassDef =>
      !x.mods.isTrait
    case _ =>
      false
  }

  /**
    * Check's if the tree is a object
    * @param wrappedTree the ast
    * @return
    */
  def isObject(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case _: wrappedTree.compiler.global.ModuleDef =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is an abstract class
    * @param wrappedTree the ast
    * @return
    */
  def isAbstractClass(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match  {
    case x: wrappedTree.compiler.global.ClassDef =>
      x.symbol.isAbstractClass
    case _ =>
      false
  }

  /**
    * Check's if the tree is an anonymous class
    * @param wrappedTree the ast
    * @return
    */
  def isAnonymousClass(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match  {
    case x: wrappedTree.compiler.global.ClassDef =>
      isClass(wrappedTree) && x.symbol.isAnonymousClass
    case _ =>
      false
  }

  /**
    * Check's if the tree is a function
    * @param wrappedTree the ast
    * @return
    */
  def isFunction(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case _: wrappedTree.compiler.global.DefDef =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is an anonymous function
    * @param wrappedTree the ast
    * @return
    */
  def isAnonymousFunction(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.Function =>
      x.symbol.isAnonymousFunction
    case _ =>
      false
  }

  /**
    * Check's if the tree is a nested function
    * @param wrappedTree the ast
    * @return
    */
  def isNestedFunction(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.DefDef =>
      x.symbol.owner.isMethod
    case _ =>
      false
  }

  /**
    * Check's if the tree is a function call
    * @param wrappedTree the ast
    * @return
    */
  def isFunctionCall(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case _: wrappedTree.compiler.global.Apply =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is an assignment
    * @param wrappedTree the ast
    * @return
    */
  def isAssignment(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match  {
    case _: wrappedTree.compiler.global.Assign =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is a new statement
    * @param wrappedTree the ast
    * @return
    */
  def isNewClass(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match  {
    case _: wrappedTree.compiler.global.New =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is a val definition
    * @param wrappedTree the ast
    * @return
    */
  def isValDef(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match  {
    case x: wrappedTree.compiler.global.ValDef =>
      !x.symbol.isMutable
    case _ =>
      false
  }

  /**
    * Check's if the tree is a var definition
    * @param wrappedTree the ast
    * @return
    */
  def isVarDef(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.ValDef =>
      x.symbol.isMutable
    case _ =>
      false
  }

  /**
    * Check's if the tree is a var
    * @param wrappedTree the ast
    * @return
    */
  def isVar(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.Ident =>
      x.symbol.isVar
    case _ =>
      false
  }

  /**
    * Check's if the tree is a val
    * @param wrappedTree the ast
    * @return
    */
  def isVal(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.Ident =>
      x.symbol.isVal
    case _ =>
      false
  }

  /**
    * Check's if the tree is for statement
    * @param wrappedTree the ast
    * @return
    */
  def isFor(wrappedTree: TreeWrapper): Boolean = {
    isCall(wrappedTree, "foreach")
  }

  /**
    * Check's if the tree is a while statement
    * @param wrappedTree the ast
    * @return
    */
  def isWhile(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.LabelDef =>
      ("""^while\$(\d)*""".r findFirstIn x.name.toString).nonEmpty
    case _ =>
      false
  }

  /**
    * Check's if the tree is a do while
    * @param wrappedTree the ast
    * @return
    */
  def isDoWhile(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.LabelDef =>
      ("""^doWhile\$(\d)*""".r findFirstIn x.name.toString).nonEmpty
    case _ =>
      false
  }

  /**
    * Check's if the tree is a function call to a specific function
    * @param wrappedTree the ast
    * @return
    */
  def isCall(wrappedTree: TreeWrapper, name: String): Boolean = wrappedTree.unWrap() match {
    case x: wrappedTree.compiler.global.Apply =>
      x.fun.symbol.name.toString == name
    case _ =>
      false
  }

  /**
    * Check's if the tree is a match statement
    * @param wrappedTree the ast
    * @return
    */
  def isMatch(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case _: wrappedTree.compiler.global.Match =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is a case statement
    * @param wrappedTree the ast
    * @return
    */
  def isCase(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case _: wrappedTree.compiler.global.CaseDef =>
      true
    case _ =>
      false
  }

  /**
    * Check's if the tree is an if statement
    * @param wrappedTree the ast
    * @return
    */
  def isIf(wrappedTree: TreeWrapper): Boolean = wrappedTree.unWrap() match {
    case _: wrappedTree.compiler.global.If =>
      true
    case _ =>
      false
  }
}
