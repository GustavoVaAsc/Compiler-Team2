package unam.fi.compilers.team2.parser

data class Production(
    val symbol: Nonterminal,
    val expressions: List<Symbol>
)
