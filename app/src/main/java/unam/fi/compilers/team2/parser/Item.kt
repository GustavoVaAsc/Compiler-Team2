package unam.fi.compilers.team2.parser

data class Item(
    val production: Production,
    val lookahead_position: Int,
    val lookahead : Terminal
){
    fun nextSymbol():Symbol? = if(lookahead_position < production.expressions.size)
                                    production.expressions[lookahead_position]
                               else null
}
