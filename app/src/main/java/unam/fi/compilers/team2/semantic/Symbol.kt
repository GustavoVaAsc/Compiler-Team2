package unam.fi.compilers.team2.semantic

data class Symbol(
    val name: String,
    val type: String,
    val kind: SymbolKind,
    val scope: Scope,
    val defined_at: Int // Line
)
