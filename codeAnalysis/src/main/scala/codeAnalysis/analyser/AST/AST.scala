package codeAnalysis.analyser.AST

import scala.reflect.internal.util.RangePosition

/**
  * Created by erikl on 4/24/2017.
  */
class AST(val children: List[AST], val pos: RangePosition)

class Module(override val children: List[AST], override val pos: RangePosition, parents: List[Parent], name: String, pack: String) extends AST(children, pos)
class Value(override val children: List[AST], override val pos: RangePosition, val name: String) extends AST(children, pos)
class ValueDefinition(override val children: List[AST], override val pos: RangePosition, val name: String) extends AST(children, pos)


case class PackageDefinition(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class TraitDefinition(override val children: List[AST], override val pos: RangePosition, parents: List[Parent], name: String, pack: String)
  extends Module(children, pos, parents, name, pack)

case class ClassDefinition(override val children: List[AST], override val pos: RangePosition, parents: List[Parent], name: String, pack: String, isAbstract: Boolean, nested: Boolean, anonymous: Boolean)
  extends Module(children, pos, parents, name, pack)

case class ObjectDefinition(override val children: List[AST], override val pos: RangePosition, parents: List[Parent], name: String, pack: String, nested: Boolean)
  extends Module(children, pos, parents, name, pack)

case class FunctionDef(override val children: List[AST], override val pos: RangePosition, name: String, owner: String, nested: Boolean, anonymous: Boolean) extends AST(children, pos)

case class FunctionCall(override val children: List[AST], override val pos: RangePosition, name: String, owner: String) extends AST(children, pos)

case class ValAssignment(override val children: List[AST], override val pos: RangePosition, variable: String) extends AST(children, pos)

case class VarAssignment(override val children: List[AST], override val pos: RangePosition, variable: String) extends AST(children, pos)

case class NewClass(override val children: List[AST], override val pos: RangePosition, name: String) extends AST(children, pos)

case class ValDefinition(override val children: List[AST], override val pos: RangePosition, override val name: String)
  extends ValueDefinition(children, pos, name)

case class VarDefinition(override val children: List[AST], override val pos: RangePosition, override val name: String)
  extends ValueDefinition(children, pos, name)

case class Var(override val children: List[AST], override val pos: RangePosition, override val name: String)
  extends Value(children, pos, name)

case class Val(override val children: List[AST], override val pos: RangePosition, override val name: String)
  extends Value(children, pos, name)

case class For(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class While(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class DoWhile(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class MatchCase(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class Case(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class CaseAlternative(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)

case class IfStatement(override val children: List[AST], override val pos: RangePosition) extends AST(children, pos)



class Parent(val name: String, val pack: String, val parents: List[Parent])

case class ClassParent(override val name: String, override val pack: String, override val parents: List[Parent]) extends Parent(name, pack, parents)

case class TraitParent(override val name: String, override val pack: String, override val parents: List[Parent]) extends Parent(name, pack, parents)


