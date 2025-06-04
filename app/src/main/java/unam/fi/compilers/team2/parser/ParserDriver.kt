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

    // Maps Lexer token types to parser Terminal objects
    private fun mapTokenToTerminal(token: Token): Terminal {
        return when (token.getTokenType()) {
            "INTEGER" -> Terminal("INTEGER")
            "FLOAT" -> Terminal("FLOAT")
            "STRING" -> Terminal("STRING")
            "Keyword" -> {
                when (token.getTokenValue().lowercase()) {
                    "import" -> Terminal("import")
                    "function" -> Terminal("function")
                    "class" -> Terminal("class")
                    "const" -> Terminal("const")
                    "type" -> Terminal("type")
                    "if" -> Terminal("if")
                    "else" -> Terminal("else")
                    "while" -> Terminal("while")
                    "return" -> Terminal("return")
                    else -> throw ParseError("Unknown keyword: ${token.getTokenValue()}")
                }
            }
            "Datatype" -> {  // Handle datatype tokens
                when (token.getTokenValue().lowercase()) {
                    "int" -> Terminal("int")
                    "float" -> Terminal("float")
                    "bool" -> Terminal("bool")
                    "string" -> Terminal("string")
                    "void" -> Terminal("void")
                    else -> Terminal("ID")  // Custom types
                }
            }
            "Boolean" -> {
                when (token.getTokenValue()) {
                    "true" -> Terminal("true")
                    "false" -> Terminal("false")
                    else -> throw ParseError("Invalid boolean value: ${token.getTokenValue()}")
                }
            }
            "Identifier" -> Terminal("ID")
            "Operator" -> Terminal(token.getTokenValue())
            "Relation" -> Terminal(token.getTokenValue())
            "Punctuation" -> Terminal(token.getTokenValue())
            else -> throw ParseError("Unknown token type: ${token.getTokenType()}")
        }
    }

    private fun classifyConstant(value: String): Terminal {
        return when {
            value.matches(Regex("-?\\d+")) -> Terminal("INTEGER")
            value.matches(Regex("-?\\d+\\.\\d*")) -> Terminal("FLOAT")
            else -> throw ParseError("Invalid constant: $value")
        }
    }
    //@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun parse(tokens: List<Token>): ASTNode?{
        val tokenStream = tokens + Token(EOF.name,"$", -1, -1)
        var index = 0

        while(true){

            val state = stateStack.last()
            val token = tokenStream[index]

            // String to Terminal
            val terminal = try {
                mapTokenToTerminal(token)
            } catch (e: ParseError) {
                println("Token conversion error: ${e.message}")
                println("Token: ${token.getTokenValue()} (${token.getTokenType()}) at ${token.getTokenLine()}:${token.getTokenColumn()}")
                return null
            }
            println("Current state ID: $state")
            println("Lookahead Lexer Token: type='${token.getTokenType()}', value='${token.getTokenValue()}'")
            println("Lookahead Parser Terminal: '${terminal.name}'")

            val actionsForState = parser.action_table.filterKeys { it.first == state }
            if (actionsForState.isEmpty()) {
                println("!!! CRITICAL: No actions defined in action_table for state $state! This is likely the root cause.")
            } else {
                println("Available actions/expected terminals for state $state:")
                actionsForState.forEach { (key, actionEntry) ->
                    // key.first is state, key.second is the Symbol (Terminal)
                    println("  - Expects Terminal: '${key.second.name}' -> Leads to Action: $actionEntry")
                }
            }
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
            if (state == 13) {
                println("State 13 actions:")
                parser.action_table.filterKeys { it.first == 13 }.forEach { (key, action) ->
                    println("  ${key.second} -> $action")
                }
            }
        }


    }

    // Still pending to check
    private fun createLeafNode(token: Token, terminal: Terminal): ASTNode {
        val line = token.getTokenLine()
        val column = token.getTokenColumn()
        val value = token.getTokenValue()

        return when (terminal.name) {
            "ID" -> IdentifierNode(value, line, column, null) // If Terminal("ID")
            "INTEGER" -> IntegerNode(value.toInt(), line, column, null) // For Terminal("INTEGER")
            "FLOAT" -> FloatNode(value.toFloat(), line, column, null)   // For Terminal("FLOAT")
            "STRING" -> StringNode(value.removeSurrounding("\""), line, column, null) // For Terminal("STRING")
            "true" -> BooleanNode(true, line, column, null)           // For Terminal("true")
            "false" -> BooleanNode(false, line, column, null)
            else -> TokenNode(terminal, value, line, column, null)
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

        val node = when (production.symbol.name) {
            "Function" -> {
                // Production: 'function' Type ID '(' Parameters? ')' Block
                FunctionNode(
                    returnType = children[1] as TypeNode,  // Type is now at index 1
                    name = children[2] as IdentifierNode,
                    parameters = children.getOrNull(4) as? ParametersNode,
                    body = children[6] as BlockNode,
                    production = production
                )
            }

            "Class" -> {
                // Production: class <Id> <ClassBlock>
                // Children: ['class', Id, ClassBlock]
                ClassNode(
                    name = children[1] as IdentifierNode,  // Skip 'class' token
                    members = (children[2] as ClassBlockNode).members,
                    production = production
                )
            }

            "Block" -> {
                // Production: { <Statement>* }
                // Children: ['{', statements..., '}']
                val statements = children
                    .subList(1, children.size - 1)  // Skip braces
                    .filterIsInstance<StatementNode>()
                BlockNode(
                    statements = statements,
                    line = (children.first() as TokenNode).line,
                    column = (children.first() as TokenNode).column,
                    production = production
                )
            }

            "IfStatement" -> {
                // Production: if ( <Expression> ) <Block> <ElsePart>?
                // Children: ['if', '(', Expression, ')', Block, ElsePart?]
                IfNode(
                    condition = children[2] as ExpressionNode,
                    thenBranch = children[4] as BlockNode,
                    elseBranch = children.getOrNull(5) as? BlockNode,
                    line = (children[0] as TokenNode).line,
                    column = (children[0] as TokenNode).column,
                    production = production
                )
            }

            "WhileStatement" -> {
                // Production: while ( <Expression> ) <Block>
                // Children: ['while', '(', Expression, ')', Block]
                WhileNode(
                    condition = children[2] as ExpressionNode,
                    body = children[4] as BlockNode,
                    line = (children[0] as TokenNode).line,
                    column = (children[0] as TokenNode).column,
                    production = production
                )
            }

            "Return" -> {
                // Production: return <Expression>? ;
                // Children: ['return', Expression?] - semicolon is not in AST
                ReturnNode(
                    expression = children.getOrNull(1),  // Index 0 is 'return'
                    line = (children[0] as TokenNode).line,
                    column = (children[0] as TokenNode).column,
                    production = production
                )
            }

            "Variable", "Constant" -> {
                DeclarationNode(
                    type = children[0] as TypeNode,
                    name = children[1] as IdentifierNode,
                    value = children.getOrNull(3) as? ExpressionNode,
                    line = children[0].line,
                    column = children[0].column,
                    production = production
                )
            }


            "Assignment" -> {
                // Production: <Id> = <Expression>
                // Children: [Id, '=', Expression]
                AssignmentNode(
                    target = children[0] as IdentifierNode,
                    value = children[2] as ExpressionNode,  // Skip '='
                    line = children[0].line,
                    column = children[0].column,
                    production = production
                )
            }

            "Expression" -> children.single()  // Forward single child

            "BinaryExpression" -> {
                // Production: <Expression> <BinaryOp> <Expression>
                BinaryOpNode(
                    left = children[0] as ExpressionNode,
                    operator = (children[1] as TokenNode).value,
                    right = children[2] as ExpressionNode,
                    line = children[0].line,
                    column = children[0].column,
                    production = production
                )
            }

            "UnaryExpression" -> {
                // Production: <UnaryOp> <Expression>
                UnaryOpNode(
                    operator = (children[0] as TokenNode).value,
                    operand = children[1] as ExpressionNode,
                    line = children[0].line,
                    column = children[0].column,
                    production = production
                )
            }

            "TypeDecl" -> {
                // Production: type <Id> = <Type> ;
                // Children: ['type', Id, '=', Type]
                TypeAliasNode(
                    name = children[1] as IdentifierNode,
                    aliasType = children[3] as TypeNode,
                    production = production
                )
            }

            "PrimitiveType" -> {
                TypeNode(
                    name = (children[0] as TokenNode).value,
                    line = children[0].line,
                    column = children[0].column,
                    production = production
                )
            }
            "SimpleType" -> {
                when (val child = children[0]) {
                    is TypeNode -> child  // Already created by PrimitiveType
                    is TokenNode -> TypeNode(  // Custom type (ID)
                        name = child.value,
                        line = child.line,
                        column = child.column,
                        production = production
                    )
                    else -> throw ParseError("Invalid child for SimpleType: $child")
                }
            }
            "Type" -> {
                if (production.expressions.size == 1) {
                    children[0] as TypeNode  // SimpleType
                } else {
                    // Array type: Type '[' ']'
                    val baseType = children[0] as TypeNode
                    TypeNode(
                        name = baseType.name,
                        arrayDimensions = baseType.arrayDimensions + 1,
                        line = children[1].line,  // Position of '['
                        column = children[1].column,
                        production = production
                    )
                }
            }

            else -> throw ParseError("Unhandled production: ${production.symbol.name}")
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