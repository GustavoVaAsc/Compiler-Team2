package unam.fi.compilers.team2.lexer

// Token class, saves the type, value, line and column of each token
class Token (private val token_type:String,
             private val token_value:String,
             private val line:Int,
             private val column:Int){

    // Override toString() to output data
    override fun toString(): String {
        return "${token_type} [$token_value] at line: $line, Col: $column"
    }

    // Getters for each attribute
    public fun getTokenType():String = token_type
    public fun getTokenValue():String = token_value
    public fun getTokenLine():Int = line
    public fun getTokenColumn():Int = column
}