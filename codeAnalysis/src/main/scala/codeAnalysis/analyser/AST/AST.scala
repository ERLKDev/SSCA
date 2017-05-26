package codeAnalysis.analyser.AST

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/24/2017.
  */
class AST(val children: List[AST], val pos: RangePosition)

abstract class Module(override val children: List[AST], override val pos: RangePosition, val parents: List[Parent], val name: String, val pack: String) extends AST(children, pos)

abstract class Value(override val children: List[AST], override val pos: RangePosition, val name: String, val owner: String, val parameter: Boolean) extends AST(children, pos)

abstract class ValueDefinition(override val children: List[AST], override val pos: RangePosition, val name: String, val owner: String, val parameter: Boolean) extends AST(children, pos)



case class PackageDefinition(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class TraitDefinition(override val children: List[AST], override val pos: RangePosition, override val parents: List[Parent], override val name: String, override val pack: String)
  extends Module(children, pos, parents, name, pack)

case class ClassDefinition(override val children: List[AST], override val pos: RangePosition, override val parents: List[Parent], override val name: String, override val pack: String, isAbstract: Boolean, nested: Boolean, anonymous: Boolean)
  extends Module(children, pos, parents, name, pack)

case class ObjectDefinition(override val children: List[AST], override val pos: RangePosition, override val parents: List[Parent], override val name: String, override val pack: String, nested: Boolean)
  extends Module(children, pos, parents, name, pack)



case class FunctionDef(override val children: List[AST], override val pos: RangePosition, name: String, owner: String, nested: Boolean, anonymous: Boolean) extends AST(children, pos)

case class ValAssignment(override val children: List[AST], override val pos: RangePosition, override val name: String, override val owner: String, override val parameter: Boolean)
  extends Value(children, pos, name, owner, parameter)

case class VarAssignment(override val children: List[AST], override val pos: RangePosition, override val name: String, override val owner: String, override val parameter: Boolean)
  extends Value(children, pos, name, owner, parameter)

case class ValDefinition(override val children: List[AST], override val pos: RangePosition, override val name: String, override val owner: String, override val parameter: Boolean)
  extends ValueDefinition(children, pos, name, owner, parameter)

case class VarDefinition(override val children: List[AST], override val pos: RangePosition, override val name: String, override val owner: String, override val parameter: Boolean)
  extends ValueDefinition(children, pos, name, owner, parameter)

case class Var(override val children: List[AST], override val pos: RangePosition, override val name: String, override val owner: String, override val parameter: Boolean)
  extends Value(children, pos, name, owner, parameter)

case class Val(override val children: List[AST], override val pos: RangePosition, override val name: String, override val owner: String, override val parameter: Boolean)
  extends Value(children, pos, name, owner, parameter)


case class NewClass(override val children: List[AST], override val pos: RangePosition, name: String) extends AST(children, pos)

case class For(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class While(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class DoWhile(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class MatchCase(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class Case(override val children: List[AST], override val pos: RangePosition, pattern: List[AST]) extends AST(children, pos)

case class CaseAlternative(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class IfStatement(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class FunctionCall(override val children: List[AST], override val pos: RangePosition, name: String, owner: String) extends AST(children, pos)




class Parent(val name: String, val pack: String, val parents: List[Parent])

case class ClassParent(override val name: String, override val pack: String, override val parents: List[Parent]) extends Parent(name, pack, parents)

case class TraitParent(override val name: String, override val pack: String, override val parents: List[Parent]) extends Parent(name, pack, parents)


