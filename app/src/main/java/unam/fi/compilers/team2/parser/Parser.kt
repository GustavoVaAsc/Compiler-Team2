package unam.fi.compilers.team2.parser

import unam.fi.compilers.team2.lexer.Token
import unam.fi.compilers.team2.lexer.Lexer

// Recursive Descent Parser implementation

// When consume() is equal to a val we are parsing a Lexer's token

class Parser(private val lexer: Lexer) { // Gets the parser as input of the constructor
    // We call Lexer's tokenize() method
    private val tokens: List<Token> = lexer.tokenize()
    private var current: Int = 0 // Current position in token stream

    // Output of derivation rules
    val derivation = StringBuilder()
    private var indentLevel = 0

    // Append normal rules derivation
    private fun log(rule: String) {
        derivation.appendLine(rule)
        derivation.appendLine()
    }

    // Append the derived rules with token cases
    private fun logToken(name: String, value: String) {
        derivation.appendLine("$name → $value")
        derivation.appendLine()
    }

    // Main function to start parsing
    fun parseProgram(): Program {
        log("Program → Declaration*") // Log first rule
        indentLevel++
        val declarations = mutableListOf<ASTNode>() // Store declarations
        // For each declaration we call parseDeclaration() and we add the result to the list
        while (!isAtEnd()) {
            declarations.add(parseDeclaration())
        }
        indentLevel--
        return Program(declarations) // Return the root node
    }

    // Function to parse declaration
    private fun parseDeclaration(): ASTNode {
        // Return based on the type of declaration
        return when (peek().getTokenValue()) {
            // Class parsing
            "class" -> {
                log("Declaration → ClassDeclaration")
                parseClassDeclaration() // Call Class parsing function
            }
            // Function parsing
            "function" -> {
                log("Declaration → FunctionDeclaration")
                parseFunctionDeclaration() // Call Function parsing function
            }
            // In any other case, it is a statement
            else -> {
                log("Declaration → Statement")
                parseStatement() // Call Statement parsing function
            }
        }
    }

    // Parse the class declaration
    private fun parseClassDeclaration(): ClassDeclaration {
        log("ClassDeclaration → 'class' Identifier '{' Declaration* '}'")
        indentLevel++
        val classToken = consume("class", "Expect 'class'.") // Consume the token for class keyword
        val name = consumeType("Identifier", "Expect class name.") // Consume the token for class identifier
        logToken("Identifier", name.getTokenValue())

        // Consume the body of the class
        consume("{", "Expect '{' before class body.")
        val members = mutableListOf<ASTNode>() // Members of the class

        // We parse every declaration until we reach "}"
        while (!check("}")) {
            members.add(parseDeclaration())
        }
        consume("}", "Expect '}' after class body.") // Consume the bracket close
        indentLevel--
        return ClassDeclaration(name.getTokenValue(), members,classToken) // Return a class declaration Node of the AST
    }

    // Parse the function declaration
    private fun parseFunctionDeclaration(): FunctionDeclaration {
        log("FunctionDeclaration → 'function' Datatype Identifier '(' ')' '{' Statement* '}'")
        indentLevel++
        consume("function", "Expect 'function'.") // Consume function keyword

        // Get the return type token or an error with consume
        val returnTypeToken = consumeType("Datatype", "Expect return type.")
        logToken("Datatype", returnTypeToken.getTokenValue())
        val returnType = returnTypeToken.getTokenValue() // Get the value (ID/value) of the return token

        // Consume the function name
        val nameToken = consumeType("Identifier", "Expect function name.")
        logToken("Identifier", nameToken.getTokenValue())
        val name = nameToken.getTokenValue() // Get the name based on the token

        consume("(", "Expect '('.") // Consume left parentheses
        consume(")", "Expect ')'.") // Consume right parentheses
        consume("{", "Expect '{' before function body.") // Consume left bracket
        val body = mutableListOf<ASTNode>() // Function statements
        while (!check("}")) {
            body.add(parseStatement()) // We parse each statement
        }
        consume("}", "Expect '}' after function body.") // Consume right bracket
        indentLevel--
        return FunctionDeclaration(returnType, name, body) // We return a Function AST node
    }

    // Function to parse statements
    private fun parseStatement(): Statement {
        indentLevel++
        // We get the statement based on its Keyword
        val stmt = when {
            // If statement case
            peek().getTokenValue() == "if" -> {
                log("Statement → IfStatement")
                parseIfStatement() // Call IfStatement parser function
            }
            // While statement case
            peek().getTokenValue() == "while" -> {
                log("Statement → WhileStatement")
                parseWhileStatement() // Call WhileStatement parser function
            }
            // For statement case
            peek().getTokenValue() == "for" -> {
                log("Statement → ForStatement")
                parseForStatement() // Call ForStatement parser function
            }
            // Return statement case
            peek().getTokenValue() == "return" -> {
                log("Statement → ReturnStatement")
                parseReturnStatement() // Call Return Statement parser function
            }
            // PrintStatement case
            peek().getTokenValue() == "writeln" -> {
                log("Statement → PrintStatement")
                parsePrintStatement() // Call PrintStatement parser function
            }
            // Datatype case
            peek().getTokenType() == "Datatype" -> {
                log("Statement → VariableDeclaration")
                parseVarDecl() // Parse variable declaration
            }
            else -> {
                log("Statement → ExpressionStatement")
                parseExpressionStatement() // In the other case we parse a expression
            }
        }
        indentLevel--
        return stmt // The node returned is based on the choice
    }


    // Variable declaration statement parser
    private fun parseVarDecl(): VariableDeclaration {
        log("VariableDeclaration → Datatype Identifier ['=' Expression] ';'")
        indentLevel++
        val typeToken = advance() // Get the datatype token
        logToken("Datatype", typeToken.getTokenValue())

        // Get the variable identifier
        val name = consumeType("Identifier", "Expect variable name.")
        logToken("Identifier", name.getTokenValue())

        // Check if the variable equals to an expression to parse
        val initializer = if (match("=")) parseExpression() else null
        consume(";", "Expect ';' after variable declaration.") // Consume ;
        indentLevel--
        return VariableDeclaration(typeToken.getTokenValue(), name.getTokenValue(), initializer, typeToken)
    }

    // IfStatement parser
    private fun parseIfStatement(): IfStatement {
        log("IfStatement → 'if' '(' Expression ')' '{' Statement* '}' ['else' '{' Statement* '}']")
        indentLevel++
        // Consume if statement
        val ifToken = consume("if", "Expect 'if'.")
        // Consume the parentheses
        consume("(", "Expect '('.")
        val condition = parseExpression() // Parse the expression inside the parentheses
        consume(")", "Expect ')'.")

        // Then block start, we consume "{"
        val thenStartToken = consume("{", "Expect '{'.")
        val thenBranch = mutableListOf<ASTNode>() // Statements
        while (!check("}")) {
            thenBranch.add(parseStatement()) // Parse all statements
        }
        // Consume end of the block
        consume("}", "Expect '}' after then branch.")

        // Process else branch if exists
        val elseBranch = if (match("else")) {
            val elseToken = previous()
            val elseStartToken = consume("{", "Expect '{'.") // Consume block start
            val elseStmts = mutableListOf<ASTNode>() // List of else statements

            // Parse and add the statements until we reach the end of the block
            while (!check("}")) {
                elseStmts.add(parseStatement())
            }
            // Conssume the end of the block
            consume("}", "Expect '}' after else branch.")
            elseStmts
        } else null
        indentLevel--

        // Return IfStatement AST node
        return IfStatement(condition, thenBranch, elseBranch, token = ifToken)
    }

    // Parse while statement
    private fun parseWhileStatement(): WhileStatement {
        log("WhileStatement → 'while' '(' Expression ')' '{' Statement* '}'")
        indentLevel++

        // Consume while keyword
        val whileToken  = consume("while", "Expect 'while'.")

        // Consume both parentheses and parse the expression inside
        consume("(", "Expect '('.")
        val condition = parseExpression()
        consume(")", "Expect ')'.")

        // Consume the body start token
        val bodyStartToken = consume("{", "Expect '{'.")
        val body = mutableListOf<ASTNode>() // Statements inside while loop
        while (!check("}")) {
            body.add(parseStatement()) // Parse each statement inside
        }
        consume("}", "Expect '}' after body.") // Consume block end
        indentLevel--
        return WhileStatement(condition, body, token = whileToken) // Return WhileStatement AST node
    }

    // Parse for statement
    private fun parseForStatement(): ForStatement {
        log("ForStatement → 'for' '(' [init] ';' [cond] ';' [update] ')' '{' Statement* '}'")
        indentLevel++

        // Consume keyword
        val forToken = consume("for", "Expect 'for'.")
        consume("(", "Expect '('.") // Consume left parentheses

        // Initializer: for(int i=0; (...)
        val init = if (check(";")) { // If we found ";" we advance to next expression
            advance()
            null
        } else {
            // Check if the variable is defined inside declaration, if it is, we parse it without semicolon
            if (peek().getTokenType() == "Datatype") parseVarDeclNoSemi() else {
                // Parse the init expression
                val expr = parseExpression()
                consume(";", "Expect ';' after initializer.") // Consume ";"
                expr
            }
        }

        // Condition: for(..., i<=n, ...);
        val condition = if (check(";")) null else parseExpression() // Check if condition exists
        consume(";", "Expect ';' after condition.") // Consume ";" to advance

        // Update: for(...;...;i=i+2)
        val update = if (check(")")) null else parseExpression() // Parse the expression if exists
        consume(")", "Expect ')' after for clauses.") // Consume ")"
        val bodyStartToken  = consume("{", "Expect '{'.") // Consume block start
        val body = mutableListOf<ASTNode>() // Statements inside for

        // Parse every statement until we reach the block end
        while (!check("}")) {
            body.add(parseStatement())
        }
        consume("}", "Expect '}' after body.") // Consume block end
        indentLevel--
        return ForStatement(init, condition, update, body, token = forToken) // Return for AST node
    }

    // Parse declarations to use in for loop
    private fun parseVarDeclNoSemi(): VariableDeclaration {
        val type = advance().getTokenValue()
        val name = consumeType("Identifier", "Expect variable name.").getTokenValue()
        val initializer = if (match("=")) parseExpression() else null
        consume(";", "Expect ';' after variable declaration.")
        return VariableDeclaration(type, name, initializer) // Return VariableDeclaration node
    }

    // Parse return statements
    private fun parseReturnStatement(): ReturnStatement {
        log("ReturnStatement → 'return' [Expression] ';'")
        indentLevel++
        // Consume return keyword
        val returnToken = consume("return", "Expect 'return'.")
        // Parse expression if exists (we could have void return)
        val value = if (!check(";")) parseExpression() else null
        consume(";", "Expect ';' after return.")
        indentLevel--
        return ReturnStatement(value, returnToken) // Return ReturnStatement AST node
    }

    // Parse program outputs
    private fun parsePrintStatement(): PrintStatement {
        log("PrintStatement → 'writeln' '(' Expression ')' ';'")
        indentLevel++
        consume("writeln", "Expect 'writeln'.") // Consume writeln function id
        consume("(", "Expect '(' after 'writeln'.") // Consume open bracket
        val value = parseExpression() // Parse expression inside writeln

        // Consume ) and ;
        consume(")", "Expect ')' after expression.")
        consume(";", "Expect ';' after statement.")
        indentLevel--
        return PrintStatement(value) // Return PrintStatement AST node
    }

    // Parse expression statement
    private fun parseExpressionStatement(): Statement {
        log("ExpressionStatement → Expression ';'")
        indentLevel++
        // Parse expression
        val expr = parseExpression()
        consume(";", "Expect ';' after expression.")
        indentLevel--
        return ExpressionStatement(expr) // Return ExpStatement AST node
    }

    // Parse Expression
    private fun parseExpression(): Expression {
        log("Expression → Assignment")
        indentLevel++
        // Call parse Assignment
        val expr = parseAssignment()
        indentLevel--
        return expr
    }


    private fun parseAssignment(): Expression {
        val expr = parseLogicalOr()
        if (match("=")) {
            if (expr is Variable) {
                val value = parseAssignment()
                return Assignment(expr.name, value, previous())
            }
            throw error(previous(), "Invalid assignment target")
        }
        return expr
    }

    private fun parseLogicalOr(): Expression {
        var expr = parseLogicalAnd()
        while (match("||")) {
            val op = previous().getTokenValue()
            logToken("Operator", op)
            val right = parseLogicalAnd()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseLogicalAnd(): Expression {
        var expr = parseEquality()
        while (match("&&")) {
            val op = previous().getTokenValue()
            logToken("Operator", op)
            val right = parseEquality()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseEquality(): Expression {
        var expr = parseComparison()
        while (match("==", "!=")) {
            val op = previous().getTokenValue()
            logToken("Operator", op)
            val right = parseComparison()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseComparison(): Expression {
        var expr = parseTerm()
        while (match(">", ">=", "<", "<=")) {
            val op = previous().getTokenValue()
            logToken("Operator", op)
            val right = parseTerm()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseTerm(): Expression {
        var expr = parseFactor()
        while (match("+", "-")) {
            val op = previous().getTokenValue()
            logToken("Operator", op)
            val right = parseFactor()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseFactor(): Expression {
        var expr = parseUnary()
        while (match("*", "/", "%")) {
            val op = previous().getTokenValue()
            logToken("Operator", op)
            val right = parseUnary()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseUnary(): Expression {
        if (match("!", "-")) {
            val op = previous().getTokenValue()
            val right = parseUnary()
            return UnaryExpression(op, right)
        }
        return parsePrimary()
    }

    private fun parsePrimary(): Expression {
        return when {
            matchType("INTEGER") -> {
                val token = previous()
                logToken("Literal", token.getTokenValue())
                Literal(token.getTokenValue().toInt())
            }
            matchType("FLOAT") -> {
                val token = previous()
                logToken("Literal", token.getTokenValue())
                Literal(token.getTokenValue().toDouble())
            }
            matchType("STRING") -> {
                val token = previous()
                logToken("Literal", token.getTokenValue())
                Literal(token.getTokenValue())
            }
            matchType("Boolean") -> {
                val token = previous()
                logToken("Literal", token.getTokenValue())
                Literal(token.getTokenValue() == "true")
            }
            matchType("Identifier") -> {
                val token = previous()
                logToken("Identifier", token.getTokenValue())
                Variable(token.getTokenValue())
            }
            match("(") -> {
                val expr = parseExpression()
                consume(")", "Expect ')' after expression.")
                Grouping(expr)
            }
            else -> throw error(peek(), "Expected expression.")
        }
    }


    private fun match(vararg values: String): Boolean {
        for (value in values) {
            if (check(value)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun matchType(tokenType: String): Boolean {
        if (!isAtEnd() && peek().getTokenType() == tokenType) {
            advance()
            return true
        }
        return false
    }

    private fun check(value: String): Boolean {
        if (isAtEnd()) return false
        return peek().getTokenValue() == value
    }

    private fun advance(): Token = tokens[current++]
    private fun peek(): Token = tokens[current]
    private fun previous(): Token = tokens[current - 1]
    private fun isAtEnd(): Boolean = current >= tokens.size

    private fun consume(expected: String, message: String): Token {
        if (check(expected)) return advance()
        throw error(peek(), message)
    }

    private fun consumeType(expectedType: String, message: String): Token {
        if (!isAtEnd() && peek().getTokenType() == expectedType) return advance()
        throw error(peek(), message)
    }

    private fun error(token: Token, message: String): RuntimeException {
        return RuntimeException("[Line ${token.getTokenLine()}] Error at '${token.getTokenValue()}': $message")
    }

    fun getDerivationOutput(): String {
        return derivation.toString()
    }
}