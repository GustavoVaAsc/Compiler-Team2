package unam.fi.compilers.team2.semantic

class SymbolTable {
    private val scopes = mutableListOf<Scope>()
    private val pendingDefinitions = mutableListOf<Symbol>()
    val global_scope = Scope("global")

    init{
        enterScope(global_scope)
    }

    public fun enterScope(scope:Scope){
        scopes.add(scope)
    }

    public fun exitScope(){
        if(scopes.size > 1) scopes.removeAt(scopes.lastIndex)
    }

    public fun currentScope():Scope = scopes.last()

    public fun define(symbol:Symbol): Boolean{
        return currentScope().define(symbol)
    }

    public fun resolve(name:String):Symbol?{
        return scopes.asReversed().firstNotNullOf { it.resolve(name) }
    }
    fun flushPendingDefinitions() {
        pendingDefinitions.forEach { define(it) }
        pendingDefinitions.clear()
    }


}