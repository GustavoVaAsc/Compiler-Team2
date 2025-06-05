package unam.fi.compilers.team2.semantic

import androidx.core.app.NotificationCompat.MessagingStyle.Message
import unam.fi.compilers.team2.parser.ASTNode
import unam.fi.compilers.team2.parser.Assignment
import unam.fi.compilers.team2.parser.BinaryExpression
import unam.fi.compilers.team2.parser.ClassDeclaration
import unam.fi.compilers.team2.parser.Expression
import unam.fi.compilers.team2.parser.ExpressionStatement
import unam.fi.compilers.team2.parser.ForStatement
import unam.fi.compilers.team2.parser.FunctionDeclaration
import unam.fi.compilers.team2.parser.Grouping
import unam.fi.compilers.team2.parser.IfStatement
import unam.fi.compilers.team2.parser.Literal
import unam.fi.compilers.team2.parser.PrintStatement
import unam.fi.compilers.team2.parser.Program
import unam.fi.compilers.team2.parser.ReturnStatement
import unam.fi.compilers.team2.parser.UnaryExpression
import unam.fi.compilers.team2.parser.Variable
import unam.fi.compilers.team2.parser.VariableDeclaration
import unam.fi.compilers.team2.parser.WhileStatement

class SemanticAnalyzer {
    private val symbol_table = SymbolTable()
    private val errors = mutableListOf<String>()
    private var current_function:FunctionDeclaration? = null

    public fun analyze(program:Program):List<String>{
        visit(program)
        return errors
    }

    private fun visit(node:ASTNode){
        when(node){
            is Program -> node.declarations.forEach{visit(it)}
            is ClassDeclaration -> visitClass(node)
            is FunctionDeclaration -> visitFunction(node)
            is VariableDeclaration -> visitVarDecl(node)
            is Assignment -> visitAssignment(node)
            is IfStatement -> visitIf(node)
            is WhileStatement -> visitWhile(node)
            is ForStatement -> visitFor(node)
            is ReturnStatement -> visitReturn(node)
            is PrintStatement -> visitPrint(node)
            is ExpressionStatement -> visit(node.expr)
            is BinaryExpression -> visitBinary(node)
            is UnaryExpression -> visitUnary(node)
            is Variable -> visitVariable(node)
            is Literal -> visitLiteral(node)
            is Grouping -> visitGrouping(node)
        }
    }

    private fun visitClass(node: ClassDeclaration){
        // Class definition in scope
        if(!symbol_table.define((Symbol(node.name,"class",SymbolKind.CLASS,symbol_table.currentScope(),-1)))){
            error("Class '${node.name}' is already defined", node)
        }

        // Class scope
        val classScope = Scope("class:${node.name}",symbol_table.currentScope())
        symbol_table.enterScope(classScope)

        // Visit class members
        node.members.forEach{visit(it)}

        symbol_table.exitScope()
    }

    private fun visitFunction(node: FunctionDeclaration){
        // Function definition in scope
        if(!symbol_table.define(Symbol(node.name, node.returnType, SymbolKind.FUNCTION, symbol_table.currentScope(),-1))){
            error("Function '${node.name}' already defined",node)
        }

        // Function scope
        val functionScope = Scope("function: ${node.name}", symbol_table.currentScope())
        symbol_table.enterScope(functionScope)
        current_function = node

        node.body.forEach{visit(it)}

        symbol_table.exitScope()
        current_function = null
    }

    private fun visitVarDecl(node: VariableDeclaration){
        // Variable definition in scope
        if(!symbol_table.define(Symbol(node.name, node.type, SymbolKind.VARIABLE, symbol_table.currentScope(),-1))){
            error("Variable '${node.name}' already defined", node)
        }

        // Initialization type
        node.value?.let{
            visit(it)
            val exprType = getType(it)
            if(exprType != node.type && exprType != "null"){
                error("Type mismatch: ${node.type} vs $exprType", node)
            }
        }
    }

    private fun visitAssignment(node:Assignment){
        val symbol = symbol_table.resolve(node.name) ?: return error("Undefined variable '${node.name}'",node)

        visit(node.value)
        val valueType = getType(node.value)

        if(symbol.type != valueType){
            error("Cannot assign $valueType to ${symbol.type}",node)
        }
    }

    private fun visitReturn(node:ReturnStatement){
        val func = current_function ?: return error("Return outside function",node)

        node.value?.let{
            visit(it)
            val returnType = getType(it)
            if(func.returnType != returnType){
                error("Return type ${func.returnType} expected, got $returnType",node)
            }
        } ?: run{
            if(func.returnType != "void"){
                error("Non-void function must return a value",node)
            }
        }
    }

    private fun visitIf(node: IfStatement){

    }

    private fun getType(expr: Expression): String {
        return when (expr) {
            is Literal -> when (expr.value) {
                is Int -> "int"
                is Double -> "float"
                is String -> "string"
                is Boolean -> "bool"
                else -> "unknown"
            }

            is Variable -> symbol_table.resolve(expr.name)?.type ?: "undefined"

            is Assignment -> getType(expr.value)  // Type of assignment is type of RHS

            is BinaryExpression -> {
                val leftType = getType(expr.left)
                val rightType = getType(expr.right)

                return when (expr.op) {
                    // Arithmetic operators
                    "+", "-", "*", "/", "%" -> {
                        if (isNumeric(leftType) && isNumeric(rightType)) {
                            // Numeric promotion: float > int
                            if (leftType == "float" || rightType == "float") "float" else "int"
                        } else if (leftType == "string" && expr.op == "+") {
                            "string"  // String concatenation
                        } else {
                            "mismatch"
                        }
                    }

                    // Comparison operators
                    ">", ">=", "<", "<=" -> {
                        if (isNumeric(leftType) && isNumeric(rightType)) "bool" else "mismatch"
                    }

                    // Equality operators
                    "==", "!=" -> {
                        if (leftType == rightType ||
                            (isNumeric(leftType) && isNumeric(rightType))) "bool" else "mismatch"
                    }

                    // Logical operators
                    "&&", "||" -> {
                        if (leftType == "bool" && rightType == "bool") "bool" else "mismatch"
                    }

                    else -> "unknown"
                }
            }

            is UnaryExpression -> {
                val exprType = getType(expr.expr)
                when (expr.op) {
                    "!" -> if (exprType == "bool") "bool" else "mismatch"
                    "-" -> if (isNumeric(exprType)) exprType else "mismatch"
                    else -> "unknown"
                }
            }

            is Grouping -> getType(expr.expr)

            // TODO? Implement function calls
            // is FunctionCall -> resolveFunctionReturnType(expr.name)

            else -> "unknown"
        }
    }

    private fun isNumeric(type: String): Boolean = type == "int" || type == "float"

    private fun error(message: String, node: ASTNode){
        errors.add("[Line ${getNodeLine(node)}] Semantic error $message")
    }

    private fun getNodeLine(node: ASTNode): Int{

        return -1
    }
}