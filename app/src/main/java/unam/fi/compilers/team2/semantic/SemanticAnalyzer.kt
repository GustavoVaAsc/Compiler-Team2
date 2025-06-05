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
        // Check if condition is boolean
        visit(node.condition)
        val conditionType = getType(node.condition)
        if(conditionType != "bool"){
            error("Condition must be bool, got $conditionType",node)
        }

        // Visit the body of then branch
        node.thenBranch.forEach{visit(it)}

        // If exists, visit else's body branch
        node.elseBranch?.forEach{visit(it)}
    }

    private fun visitWhile(node: WhileStatement){
        // Check if the condition is bool
        visit(node.condition)
        val conditionType = getType(node.condition)
        if(conditionType != "bool"){
            error("Condition must be bool, got $conditionType",node)
        }

        // Visit loop body
        node.body.forEach{visit(it)}
    }

    private fun visitFor(node: ForStatement){
        // Visit initializer if exists for([int i=0]; ...)
        node.init?.let{ visit(it)}

        // Check condition if exists for(...; [i<n]; ...)
        node.condition?.let{
            visit(it)
            val conditionType = getType(it)
            if(conditionType != "bool"){
                error("Condition must be boolean, got $conditionType",node)
            }
        }

        // Visit update if exists for(...; ...; i=i+1)
        node.update?.let{ visit(it)}

        // Visit loop body
        node.body.forEach{ visit(it) }
    }

    private fun visitPrint(node:PrintStatement){
        visit(node.value)
    }

    private fun visitBinary(node: BinaryExpression){
        // Visit the two operands
        visit(node.left)
        visit(node.right)

        val leftType = getType(node.left)
        val rightType = getType(node.right)

        if(leftType == "undefined"){
            error("Undefined variable in left operand", node.left)
        }
        if(rightType == "undefined"){
            error("Undefined variable in right operand", node.right)
        }

        // Check for type mismatches
        if(!isCompatible(leftType,rightType,node)){
            error("Type mismatch: $leftType ${node.op} $rightType", node)
        }
    }

    private fun visitUnary(node:UnaryExpression){
        // Visit operand
        visit(node.expr)

        // Get operand
        val expressionType = getType(node.expr)

        // Check if the variable is undefined
        if(expressionType == "undefined"){
            error("Undefined variable in expression", node.expr)
        }

        // Check operator compatibility
        when(node.op){
            "!" -> {
                if (expressionType != "bool"){
                    error("'!' operator requires boolean operand, got $expressionType", node)
                }
            }
        }
    }

    private fun visitVariable(node: Variable){
        // Check if variable is already defined
        if(symbol_table.resolve(node.name) == null){
            error("Undefined variable '${node.name}'",node)
        }
    }

    private fun visitLiteral(node:Literal){

    }

    private fun visitGrouping(node: Grouping){
        visit(node.expr)
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

    // TODO: Implement function classes and class members

    private fun isNumeric(type: String): Boolean = type == "int" || type == "float"

    private fun isCompatible(type1: String, type2:String, node:BinaryExpression): Boolean{
        // TODO: Add a message saying strings are not operable
        return when{
            type1 == type2 -> true
            isNumeric(type1) && isNumeric(type2) -> true
            //type1 == "string" && type2 == "string" && node.op =="+" ->true
            else -> false
        }
    }

    private fun error(message: String, node: ASTNode){
        errors.add("[Line ${getNodeLine(node)}] Semantic error $message")
    }

    private fun getNodeLine(node: ASTNode): Int{

        return -1
    }
}