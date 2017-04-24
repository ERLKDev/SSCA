package analyser.Compiler

/**
  * Created by erikl on 4/24/2017.
  */
class SymbolWrapper(val compiler: CompilerS) {
  private var symbol: compiler.global.Symbol = _

  def wrap(symbol: compiler.global.Symbol): SymbolWrapper = {
    this.symbol = symbol
    this
  }

  def wrap(symbol: compiler.global.Symbol): SymbolWrapper = {
    this.symbol = symbol
    this
  }

  def unWrap(): compiler.global.Symbol = {
    symbol
  }
}
