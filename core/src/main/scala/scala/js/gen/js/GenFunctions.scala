package scala.js.gen.js

import scala.virtualization.lms.common.{BaseGenFunctions, TupledFunctionsExp}

trait GenFunctions extends GenEffect with BaseGenFunctions {
  val IR: TupledFunctionsExp
  import IR._

  override def emitNode(sym: Sym[Any], rhs: Def[Any]) = rhs match {
    case Lambda(fun, UnboxedTuple(xs), y) =>
      stream.println("var " + quote(sym) + " = function" + xs.map(quote).mkString("(", ",", ")") + " {")
      emitBlock(y)
      val result = getBlockResult((y))
      if (!(result.tp <:< manifest[Unit])) {
        stream.println("return " + quote(result))
      }
      stream.println("};")

    case Lambda(fun, x, y) =>
      stream.println("var " + quote(sym) + " = function(" + quote(x) + ") {")
      emitBlock(y)
      val result = getBlockResult((y))
      if (!(result.tp <:< manifest[Unit])) {
        stream.println("return " + quote(result))
      }
      stream.println("};")

    case Apply(fun, UnboxedTuple(args)) =>
      emitValDef(sym, quote(fun) + args.map(quote).mkString("(", ",", ")"))

    case Apply(fun, arg) =>
      emitValDef(sym, quote(fun) + "(" + quote(arg) + ")")

    case _ => super.emitNode(sym, rhs)
  }

  override def quote(x: Exp[Any]) : String = x match {
    case UnboxedTuple(args) =>
      args.zipWithIndex.map( { case (v,i) => "_" + (i+1) + ":" + quote(v) } ).mkString("{", ",", "}")
    case _ => super.quote(x)
  }
}