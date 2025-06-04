package unam.fi.compilers.team2.parser

import android.content.Context
import unam.fi.compilers.team2.lexer.Lexer

class ParserTester(val context: Context) {
    public fun testParser() {
        val source = """
        import io;
        
        function int main() {
            return 0;
        }
    """.trimIndent()

        val sourceLines = ArrayList<StringBuilder>().apply {
            source.lines().forEach { line ->
                add(StringBuilder(line))
            }
        }

        // Lexical analysis
        val lexer = Lexer(sourceLines, context)
        val tokens = lexer.tokenize()

        println("Token stream:")
        tokens.forEach { println(it) }
        println("End tokens\n")

        try {
            // Initialize parser
            val parser = Parser()
            parser.buildStates()  // Build LR(1) states
            parser.mergeStates()  // Merge to LALR(1)
            parser.buildTables()  // Build action and goto tables

            // Create the parser driver
            val driver = ParserDriver(parser)
            val astRoot = driver.parse(tokens)

            if (astRoot != null) {
                println("Parsing successful!")
                //println("Parsing successful! AST structure:")
                //printAST(astRoot)
            } else {
                println("Parsing completed but no AST returned")
            }
        } catch (e: ParseError) {
            println("Parsing failed: ${e.message}")
        }
    }


}