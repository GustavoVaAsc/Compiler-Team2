package unam.fi.compilers.team2.lexer

class Token (private val token_type:String,
             private val token_value:String,
             private val line:Int,
             private val column:Int){

    override fun toString(): String {
        return "Token of type "+token_type+" with value "+token_value+" at line: "+this.line+", Col: "+column
    }

}