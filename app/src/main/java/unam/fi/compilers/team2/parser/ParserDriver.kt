package unam.fi.compilers.team2.parser

import android.os.Build
import androidx.annotation.RequiresApi
import unam.fi.compilers.team2.lexer.Token
import kotlin.math.exp

class ParserDriver (private val parser: Parser) {
    private val stateStack = mutableListOf<Int>().apply { add(0) }
    private val symbolStack = mutableListOf<Symbol>()
    private val valueStack = mutableListOf<ASTNode>()
    private val EOF = Terminal("$")

    // Map token type strings to Terminal objects
    private val tokenTypeToTerminal = mutableMapOf<String, Terminal>().apply {
        // Keywords
        put("import", Terminal("import"))
        put("function", Terminal("function"))
        put("class", Terminal("class"))
        put("const", Terminal("const"))
        put("type", Terminal("type"))
        put("if", Terminal("if"))
        put("else", Terminal("else"))
        put("while", Terminal("while"))
        put("return", Terminal("return"))

        // Primitive types
        put("int8", Terminal("int8"))
        put("int16", Terminal("int16"))
        put("int32", Terminal("int32"))
        put("int64", Terminal("int64"))
        put("float32", Terminal("float32"))
        put("float64", Terminal("float64"))
        put("bool", Terminal("bool"))
        put("char", Terminal("char")) // Maybe this isn't fully needed
        put("string", Terminal("string"))
        put("void", Terminal("void"))

        // Literals
        put("true", Terminal("true"))
        put("false", Terminal("false"))
        put("INTEGER", Terminal("INTEGER"))
        put("FLOAT", Terminal("FLOAT"))
        put("STRING", Terminal("STRING"))
        put("ID", Terminal("ID"))

        // Operators and punctuation (Pending to be checked)
        put("+", Terminal("+"))
        put("-", Terminal("-"))
        put("*", Terminal("*"))
        put("/", Terminal("/"))
        put("%", Terminal("%"))
        put("==", Terminal("=="))
        put("!=", Terminal("!="))
        put("<", Terminal("<"))
        put(">", Terminal(">"))
        put("<=", Terminal("<="))
        put(">=", Terminal(">="))
        put("&&", Terminal("&&"))
        put("||", Terminal("||"))
        put("!", Terminal("!"))
        put("=", Terminal("="))
        put(";", Terminal(";"))
        put(",", Terminal(","))
        put("(", Terminal("("))
        put(")", Terminal(")"))
        put("{", Terminal("{"))
        put("}", Terminal("}"))
        put("[", Terminal("["))
        put("]", Terminal("]"))
        put("$", Terminal("$"))  // EOF
    }


    //@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun parse(tokens: List<Token>): ASTNode?{
        val tokenStream = tokens + Token(EOF.name,"$", -1, -1)
        var index = 0

        while(true){
            val state = stateStack.last()
            val token = tokenStream[index]

            // String to Terminal
            val terminal = tokenTypeToTerminal[token.getTokenType()]?: throw ParseError("Unknown token type: ${token.getTokenType()}")
            val action = parser.action_table[state to terminal] ?: run{
                recoverFromError(token,state)
                return null // Or continue for error recovery
            }

            when(action){
                is Action.Shift -> {
                    stateStack.add(action.state_id)
                    symbolStack.add(terminal)
                    valueStack.add(createLeafNode(token,terminal))
                    index++
                }
                is Action.Reduce -> {
                    handleReduce(action.production)
                }
                is Action.Accept ->{
                    return valueStack.singleOrNull() // Root AST Node
                }
            }
        }
    }

    // Still pending to check
    private fun createLeafNode(token: Token, terminal: Terminal):ASTNode{
        return when(terminal.name){
            "ID" -> IdentifierNode(token.getTokenValue())
            "INTEGER" -> IntegerNode(token.getTokenValue().toInt())
            "FLOAT" -> FloatNode(token.getTokenValue().toFloat())
            "STRING" -> StringNode(token.getTokenValue())
            "true", "false" -> BooleanNode(token.getTokenValue().toBoolean())
            else -> TokenNode(terminal, token.getTokenValue())
        }
    }

    private fun handleReduce(production: Production){
        // Pop states and symbols
        repeat(production.expressions.size){
            stateStack.removeAt(stateStack.lastIndex)
            symbolStack.removeAt(symbolStack.lastIndex)
        }

        // Build AST node from children
        val children = mutableListOf<ASTNode>()
        repeat(production.expressions.size){
            children.add(0,valueStack.removeAt(valueStack.lastIndex)) // Reverse
        }

        val node = when(production.symbol.name){
            "Function" -> FunctionNode(
                children[0] as TypeNode,
                children[1] as IdentifierNode,
                children[2] as? ParametersNode,
                children[3] as BlockNode
            )
            "Class" -> ClassNode(
                children[0] as IdentifierNode,
                children[1] as List<ClassMemberNode>
            )
            // TODO: Add the other node types
            else -> GenericNode(production.symbol.name,children)
        }
        valueStack.add(node)

        // GOTO Transition
        val prevState = stateStack.last()
        val gotoState = parser.goto_table[prevState to production.symbol]?:throw ParseError("Missing GOTO for ${production.symbol}")
        stateStack.add(gotoState)
    }

    private fun recoverFromError(token:Token, state:Int){
        val expected = parser.action_table
            .filterKeys { it.first == state }
            .keys.map{ it.second.name}
            .joinToString(", ")
        println("Syntax error at line ${token.getTokenLine()}, column ${token.getTokenColumn()}")
        println("Found: '${token.getTokenValue()}' (${token.getTokenType()})")
        println("Expected: $expected")

        throw ParseError("Unrecoverable Syntax Error!")
    }
}