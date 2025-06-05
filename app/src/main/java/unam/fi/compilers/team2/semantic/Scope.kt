package unam.fi.compilers.team2.semantic

class Scope (val name: String, val enclosingScope: Scope? = null){
    private val symbols = mutableMapOf<String,Symbol>()

    public fun define(symbol: Symbol): Boolean{
        if(symbols.containsKey((symbol.name))) return false
        symbols[symbol.name] = symbol
        return true
    }

    public fun resolve(name:String): Symbol?{
        return symbols[name] ?: enclosingScope?.resolve(name)
    }
}