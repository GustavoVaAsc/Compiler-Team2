/*package unam.fi.compilers.team2.semantic

import unam.fi.compilers.team2.parser.ASTNode
import unam.fi.compilers.team2.parser.Assignment
import unam.fi.compilers.team2.parser.BinaryExpression
import unam.fi.compilers.team2.parser.ClassDeclaration
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
    private val current_function:FunctionDeclaration? = null

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
        // Define the class in the current scope
        if(!symbol_table.define((Symbol(node.name,"class",SymbolKind.CLASS,symbol_table.currentScope(),-1)))){
            error("Class '${node.name}' is already defined")
        }

        // Class scope
        val classScope = Scope("class:${node.name}",symbol_table.currentScope())
        symbol_table.enterScope(classScope)

        // Visit class members
        node.members.forEach{visit(it)}

        symbol_table.exitScope()
    }

    private fun visitFunction(node: FunctionDeclaration){
        // Define function in the current scope
        if(!symbol_table.define(Symbol(node.name, node.returnType, SymbolKind.FUNCTION, symbol_table.currentScope(),-1))){
            error("Function")
        }
    }
}

*/
