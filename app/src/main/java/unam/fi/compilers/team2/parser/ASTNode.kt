package unam.fi.compilers.team2.parser

// Base class
sealed class ASTNode()

// Program
data class Program(val declarations: List<ASTNode>) : ASTNode() {
    override fun toString() = "Program(declarations=[\n${declarations.joinToString(",\n")}\n])"
}

// Declarations
data class ClassDeclaration(val name: String, val members: List<ASTNode>) : ASTNode() {
    override fun toString() = "ClassDeclaration(name='$name', members=[\n${members.joinToString(",\n")}\n])"
}
data class FunctionDeclaration(val returnType: String, val name: String, val body: List<ASTNode>) : ASTNode() {
    override fun toString() = "FunctionDeclaration(returnType='$returnType', name='$name', body=[\n${body.joinToString(",\n")}\n])"
}

data class VariableDeclaration(val type: String, val name: String, val value: Expression?) : Statement() {
    override fun toString() = "VariableDeclaration(type='$type', name='$name', value=${value?.toString() ?: "null"})"
}

// Statements
sealed class Statement : ASTNode()

data class ExpressionStatement(val expr: Expression) : Statement() {
    override fun toString() = "ExpressionStatement(expr=$expr)"
}


data class Assignment(val name: String, val value: Expression) : Expression() {
    override fun toString() = "Assignment(name='$name', value=$value)"
}

data class IfStatement(val condition: Expression, val thenBranch: List<ASTNode>, val elseBranch: List<ASTNode>?) : Statement() {
    override fun toString() = "IfStatement(\n" +
            "  condition=$condition,\n" +
            "  thenBranch=[\n${thenBranch.joinToString(",\n")}\n  ],\n" +
            "  elseBranch=${elseBranch?.joinToString(",\n") ?: "null"}\n)"
}
data class WhileStatement(val condition: Expression, val body: List<ASTNode>) : Statement() {
    override fun toString() = "WhileStatement(\n" +
            "  condition=$condition,\n" +
            "  body=[\n${body.joinToString(",\n")}\n  ]\n)"
}
data class ForStatement(val init: ASTNode?, val condition: Expression?, val update: Expression?, val body: List<ASTNode>) : Statement() {
    override fun toString() = "ForStatement(\n" +
            "  init=$init,\n" +
            "  condition=$condition,\n" +
            "  update=$update,\n" +
            "  body=[\n${body.joinToString(",\n")}\n  ]\n)"
}

data class ReturnStatement(val value: Expression?) : Statement() {
    override fun toString() = "ReturnStatement(value=${value?.toString() ?: "null"})"
}
data class PrintStatement(val value: Expression) : Statement() {
    override fun toString() = "PrintStatement(value=$value)"
}

// Expressions
sealed class Expression : ASTNode()
data class BinaryExpression(val left: Expression, val op: String, val right: Expression) : Expression() {
    override fun toString() = "BinaryExpression($left $op $right)"
}

data class UnaryExpression(val op: String, val expr: Expression) : Expression() {
    override fun toString() = "UnaryExpression($op$expr)"
}

data class Literal(val value: Any) : Expression() {
    override fun toString() = "Literal(${value.toString()})"
}

data class Variable(val name: String) : Expression() {
    override fun toString() = "Variable($name)"
}

data class Grouping(val expr: Expression) : Expression() {
    override fun toString() = "Grouping($expr)"
}