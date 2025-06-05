package unam.fi.compilers.team2.parser

import unam.fi.compilers.team2.lexer.Token
import unam.fi.compilers.team2.lexer.Lexer

/* K* GRAMMAR IN EBNF

Program         ::= { Declaration }

Declaration     ::= ClassDecl | FunctionDecl | Statement

ClassDecl       ::= "class" Identifier "{" { Declaration } "}"

FunctionDecl    ::= "function" Datatype Identifier "(" ")" "{" { Statement } "}"

Statement       ::= IfStatement
                 | WhileStatement
                 | ForStatement
                 | ReturnStatement
                 | PrintStatement
                 | VarDecl ";"
                 | ExpressionStatement

IfStatement     ::= "if" "(" Expression ")" "{" { Statement } "}" [ "else" "{" { Statement } "}" ]

WhileStatement  ::= "while" "(" Expression ")" "{" { Statement } "}"

ForStatement    ::= "for" "(" [VarDecl | Expression] ";" Expression? ";" Expression? ")" "{" { Statement } "}"

ReturnStatement ::= "return" [ Expression ] ";"

PrintStatement  ::= "writeln" "(" Expression ")" ";"

VarDecl         ::= Datatype Identifier [ "=" Expression ]

ExpressionStatement ::= Expression ";"

Expression      ::= Assignment

Assignment      ::= LogicalOr [ "=" Assignment ]
                 | LogicalOr

LogicalOr       ::= LogicalAnd { "||" LogicalAnd }

LogicalAnd      ::= Equality { "&&" Equality }

Equality        ::= Comparison { ("==" | "!=") Comparison }

Comparison      ::= Term { (">" | ">=" | "<" | "<=") Term }

Term            ::= Factor { ("+" | "-") Factor }

Factor          ::= Unary { ("*" | "/" | "%") Unary }

Unary           ::= ( "!" | "-" ) Unary
                 | Primary

Primary         ::= Identifier
                 | Literal
                 | "(" Expression ")"
                 | BooleanLiteral

Literal         ::= INTEGER | FLOAT | STRING

BooleanLiteral  ::= "true" | "false"

*/

class Parser(private val lexer: Lexer) {
    private val tokens: List<Token> = lexer.tokenize()
    private var current: Int = 0

    fun parseProgram(): Program {
        val declarations = mutableListOf<ASTNode>()
        while (!isAtEnd()) {
            declarations.add(parseDeclaration())
        }
        return Program(declarations)
    }

    private fun parseDeclaration(): ASTNode {
        return when (peek().getTokenValue()) {
            "class" -> parseClassDeclaration()
            "function" -> parseFunctionDeclaration()
            else -> parseStatement()
        }
    }

    private fun parseClassDeclaration(): ClassDeclaration {
        consume("class", "Expect 'class'.")
        val name = consumeType("Identifier", "Expect class name.")
        consume("{", "Expect '{' before class body.")
        val members = mutableListOf<ASTNode>()
        while (!check("}")) {
            members.add(parseDeclaration())
        }
        consume("}", "Expect '}' after class body.")
        return ClassDeclaration(name.getTokenValue(), members)
    }

    private fun parseFunctionDeclaration(): FunctionDeclaration {
        consume("function", "Expect 'function'.")
        val returnType = consumeType("Datatype", "Expect return type.").getTokenValue()
        val name = consumeType("Identifier", "Expect function name.").getTokenValue()
        consume("(", "Expect '('.")
        consume(")", "Expect ')'.") // Simplified: no parameters
        consume("{", "Expect '{' before function body.")
        val body = mutableListOf<ASTNode>()
        while (!check("}")) {
            body.add(parseStatement())
        }
        consume("}", "Expect '}' after function body.")
        return FunctionDeclaration(returnType, name, body)
    }

    private fun parseStatement(): Statement {
        return when {
            peek().getTokenValue() == "if" -> parseIfStatement()
            peek().getTokenValue() == "while" -> parseWhileStatement()
            peek().getTokenValue() == "for" -> parseForStatement()
            peek().getTokenValue() == "return" -> parseReturnStatement()
            peek().getTokenValue() == "writeln" -> parsePrintStatement()
            peek().getTokenType() == "Datatype" -> parseVarDecl()
            else -> parseExpressionStatement()
        }
    }

    private fun parseVarDecl(): Statement {
        val type = advance().getTokenValue()  // Consume datatype
        val name = consumeType("Identifier", "Expect variable name.").getTokenValue()
        val initializer = if (match("=")) parseExpression() else null
        consume(";", "Expect ';' after variable declaration.")
        return VariableDeclaration(type, name, initializer)
    }

    private fun parseIfStatement(): IfStatement {
        consume("if", "Expect 'if'.")
        consume("(", "Expect '('.")
        val condition = parseExpression()
        consume(")", "Expect ')'.")
        consume("{", "Expect '{'.")
        val thenBranch = mutableListOf<ASTNode>()
        while (!check("}")) {
            thenBranch.add(parseStatement())
        }
        consume("}", "Expect '}' after then branch.")
        val elseBranch = if (match("else")) {
            consume("{", "Expect '{'.")
            val elseStmts = mutableListOf<ASTNode>()
            while (!check("}")) {
                elseStmts.add(parseStatement())
            }
            consume("}", "Expect '}' after else branch.")
            elseStmts
        } else null
        return IfStatement(condition, thenBranch, elseBranch)
    }

    private fun parseWhileStatement(): WhileStatement {
        consume("while", "Expect 'while'.")
        consume("(", "Expect '('.")
        val condition = parseExpression()
        consume(")", "Expect ')'.")
        consume("{", "Expect '{'.")
        val body = mutableListOf<ASTNode>()
        while (!check("}")) {
            body.add(parseStatement())
        }
        consume("}", "Expect '}' after body.")
        return WhileStatement(condition, body)
    }

    private fun parseForStatement(): ForStatement {
        consume("for", "Expect 'for'.")
        consume("(", "Expect '('.")

        // Initializer (optional)
        val init = if (check(";")) {
            advance() // Consume ;
            null
        } else {
            if (peek().getTokenType() == "Datatype") {
                parseVarDeclNoSemi()
            } else {
                val expr = parseExpression()
                consume(";", "Expect ';' after initializer.")
                expr
            }
        }

        // Condition (optional)
        val condition = if (check(";")) null else parseExpression()
        consume(";", "Expect ';' after condition.")

        // Update (optional)
        val update = if (check(")")) null else parseExpression()
        consume(")", "Expect ')' after for clauses.")

        consume("{", "Expect '{'.")
        val body = mutableListOf<ASTNode>()
        while (!check("}")) {
            body.add(parseStatement())
        }
        consume("}", "Expect '}' after body.")
        return ForStatement(init, condition, update, body)
    }

    private fun parseVarDeclNoSemi(): VariableDeclaration {
        val type = advance().getTokenValue()  // Consume datatype
        val name = consumeType("Identifier", "Expect variable name.").getTokenValue()
        val initializer = if (match("=")) parseExpression() else null
        consume(";", "Expect ';' after variable declaration.")
        return VariableDeclaration(type, name, initializer)
    }

    private fun parseReturnStatement(): ReturnStatement {
        consume("return", "Expect 'return'.")
        val value = if (!check(";")) parseExpression() else null
        consume(";", "Expect ';' after return.")
        return ReturnStatement(value)
    }

    private fun parsePrintStatement(): PrintStatement {
        consume("writeln", "Expect 'writeln'.")
        consume("(", "Expect '(' after 'writeln'.")
        val value = parseExpression()
        consume(")", "Expect ')' after expression.")
        consume(";", "Expect ';' after statement.")
        return PrintStatement(value)
    }

    private fun parseExpressionStatement(): Statement {
        val expr = parseExpression()
        consume(";", "Expect ';' after expression.")
        return ExpressionStatement(expr)
    }

    // Expression parsing with logical operators and assignment
    private fun parseExpression(): Expression = parseAssignment()

    private fun parseAssignment(): Expression {
        val expr = parseLogicalOr()

        if (match("=")) {
            if (expr is Variable) {
                val value = parseAssignment()
                return Assignment(expr.name, value)  // Now valid Expression
            }
            throw error(previous(), "Invalid assignment target")
        }

        return expr
    }

    private fun parseLogicalOr(): Expression {
        var expr = parseLogicalAnd()
        while (match("||")) {
            val op = previous().getTokenValue()
            val right = parseLogicalAnd()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseLogicalAnd(): Expression {
        var expr = parseEquality()
        while (match("&&")) {
            val op = previous().getTokenValue()
            val right = parseEquality()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseEquality(): Expression {
        var expr = parseComparison()
        while (match("==", "!=")) {
            val op = previous().getTokenValue()
            val right = parseComparison()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseComparison(): Expression {
        var expr = parseTerm()
        while (match(">", ">=", "<", "<=")) {
            val op = previous().getTokenValue()
            val right = parseTerm()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseTerm(): Expression {
        var expr = parseFactor()
        while (match("+", "-")) {
            val op = previous().getTokenValue()
            val right = parseFactor()
            expr = BinaryExpression(expr, op, right)
        }
        return expr
    }

    private fun parseFactor(): Expression {
        var expr = parseUnary()
        while (match("*", "/", "%")) {
            val op = previous().getTokenValue()
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
            matchType("INTEGER") -> Literal(previous().getTokenValue().toInt())
            matchType("FLOAT") -> Literal(previous().getTokenValue().toDouble())
            matchType("STRING") -> Literal(previous().getTokenValue())
            matchType("Boolean") -> Literal(previous().getTokenValue() == "true")
            matchType("Identifier") -> Variable(previous().getTokenValue())
            match("(") -> {
                val expr = parseExpression()
                consume(")", "Expect ')' after expression.")
                Grouping(expr)
            }
            else -> throw error(peek(), "Expected expression.")
        }
    }

    // Token helper functions
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
}