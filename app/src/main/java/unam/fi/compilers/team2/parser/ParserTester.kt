package unam.fi.compilers.team2.parser

import android.content.Context
import unam.fi.compilers.team2.lexer.Lexer
import unam.fi.compilers.team2.lexer.Token

class ParserTester(val context: Context) {
    public fun testParser() {
        val source = """
        const int MAX = 1005;
        
        function int main() {
            writeln("Hola mundo!");
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
                println("AST root: ${astRoot::class.simpleName}")

                // Print AST if available
                if (astRoot is ASTNode) {
                    println("\nAbstract Syntax Tree:")
                    printAST(astRoot)
                }
            } else {
                println("Parsing completed but no AST returned")
            }
            // Print parser tables for debugging
            /*
            println("Action Table:")
            parser.action_table.forEach { (key, action) ->
                println("State ${key.first} + Terminal '${key.second.name}': $action")
            }

            println("\nGOTO Table:")
            parser.goto_table.forEach { (key, state) ->
                println("State ${key.first} + Nonterminal '${key.second.name}': $state")
            }
            */
        } catch (e: Exception) {
            println("Parsing failed: ${e.message}")
            e.printStackTrace()
        }


    }

    private fun printAST(node: ASTNode, indent: String = "", isLast: Boolean = true) {
        val marker = if (isLast) "└── " else "├── "

        // Print current node
        print(indent + marker)
        print(node::class.simpleName)

        // Print node-specific details
        when (node) {
            is IdentifierNode -> print(": ${node.name}")
            is IntegerNode -> print(": ${node.value}")
            is FloatNode -> print(": ${node.value}")
            is StringNode -> print(": \"${node.value}\"")
            is BooleanNode -> print(": ${node.value}")
            is TypeNode -> print(": ${node.name}${if (node.isArray) "[]" else ""}")
            is TokenNode -> print(": ${node.terminal.name} = '${node.value}'")
            is BinaryOpNode -> print(": ${node.operator}")
            is UnaryOpNode -> print(": ${node.operator}")
        }

        // Print position
        println(" [${node.line}:${node.column}]")

        // Collect children - only ASTNode objects
        val children = when (node) {
            is BlockNode -> node.statements
            is FunctionNode -> listOfNotNull(node.returnType, node.name, node.parameters, node.body)
            is ClassNode -> node.members
            is ParametersNode -> node.parameters
            is IfNode -> listOfNotNull(node.condition, node.thenBranch, node.elseBranch)
            is WhileNode -> listOfNotNull(node.condition, node.body)
            is ReturnNode -> listOfNotNull(node.expression)
            is DeclarationNode -> listOfNotNull(node.type, node.name, node.value)
            is AssignmentNode -> listOfNotNull(node.target, node.value)
            is BinaryOpNode -> listOf(node.left, node.right) // Only left and right are ASTNodes
            is UnaryOpNode -> listOf(node.operand) // Only operand is ASTNode
            else -> emptyList()
        }

        // Recursively print children
        val newIndent = indent + if (isLast) "    " else "│   "
        children.forEachIndexed { index, child ->
            printAST(child, newIndent, index == children.lastIndex)
        }
    }

}