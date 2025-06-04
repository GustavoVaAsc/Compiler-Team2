package unam.fi.compilers.team2.parser

import unam.fi.compilers.team2.lexer.Lexer
import unam.fi.compilers.team2.lexer.Token
import android.content.Context
import java.io.StringReader

class ParserTester(private val context: Context) {
    private val defaultSourceCode = """
        class Perro{
            string nombre;
        
            function void ladrar(){
                  writeln("Woof woof");
             }
        }

        function void main(){
             int x = 5+2;
             writeln(x);
        }
    """.trimIndent()

    fun testParser() {
        try {
            println("Starting parser test...")
            println("Source code:\n$defaultSourceCode\n")

            val sourceLines: ArrayList<StringBuilder> = ArrayList(
                defaultSourceCode.lines().map { StringBuilder(it) }
            )

            // Create lexer with source code
            val lexer = Lexer(sourceLines,context)

            // Parse the program
            val parser = Parser(lexer)
            val program = parser.parseProgram()

            println("✅ Parsing completed successfully!")
            println("\nGenerated AST:")
            println(program)
        } catch (e: Exception) {
            println("❌ Parsing failed: ${e.message}")
        }
    }

    private fun createLexer(source: String): Lexer {
        val lines = source.split("\n")
        val lexemes = lines.map { StringBuilder(it) } as ArrayList<StringBuilder>
        return Lexer(lexemes, context)
    }
}




