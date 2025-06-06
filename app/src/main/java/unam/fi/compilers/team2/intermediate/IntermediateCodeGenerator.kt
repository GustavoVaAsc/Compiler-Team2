package unam.fi.compilers.team2.intermediate

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

class IntermediateCodeGenerator {
    private val instructions = mutableListOf<IRInstruction>()
    private var temp_counter = 0
    private var label_counter = 0
    private val symbol_table = mutableMapOf<String,String>()

    private fun newTemp() = "t${temp_counter++}"
    private fun newLabel() = "L${label_counter++}"

    public fun generate(node:ASTNode):List<IRInstruction>{
        visit(node)
        return instructions
    }

    private fun visit(node:ASTNode){
        when(node){
            is Program -> node.declarations.forEach{visit(it)}
            is FunctionDeclaration -> visitFunction(node)
            is ClassDeclaration -> visitClass(node)
            is VariableDeclaration -> visitVarDecl(node)
            is ExpressionStatement -> visit(node)
            is ReturnStatement -> visitReturn(node)
            is PrintStatement -> visitPrint(node)
            is IfStatement -> visitIf(node)
            is WhileStatement -> visitWhile(node)
            is ForStatement -> visitFor(node)
            is Assignment -> visitAssignment(node)
            is BinaryExpression -> visitBinary(node)
            is UnaryExpression -> visitUnary(node)
            is Variable -> visitVariable(node)
            is Literal -> visitLiteral(node)
            is Grouping -> visitGrouping(node)
        }
    }

    private fun visitFunction(node:FunctionDeclaration){
        instructions.add(Label("func_${node.name}_start"))
        node.body.forEach{visit(it)}
        instructions.add(Label("func_${node.name}_end"))
    }

    private fun visitClass(node: ClassDeclaration){
        // Maybe class_start/class_end?
        node.members.forEach{ visit(it)}
    }

    private fun visitVarDecl(node:VariableDeclaration){
        // Allocate space for variable
        symbol_table[node.name] = node.name

        // Initialize
        node.value?.let{
            val value = visitExpression(it)
            instructions.add(Assign(node.name,value))
        }
    }

    private fun visitReturn(node:ReturnStatement){
        val value = node.value?.let{ visitExpression(it) } ?: "void"
        instructions.add(Return(value))
    }

    private fun visitPrint(node: PrintStatement){
        val value = visitExpression(node.value)
        instructions.add(IRPrint(value))
    }

    private fun visitIf(node: IfStatement){
        val elseLabel = newLabel()
        val endLabel = newLabel()

        // Generate the condition
        val conditionTemp = visitExpression(node.condition)
        instructions.add(IfGoto(conditionTemp,elseLabel))

        // Then branch
    }

    private fun visitWhile(node: WhileStatement){
        val loopLabel = newLabel()
        val endLabel = newLabel()

        instructions.add(Label(loopLabel))

        // Generate condition
        val condTemp = visitExpression(node.condition)
        instructions.add(IfGoto(condTemp, endLabel))

        // Loop body
        node.body.forEach{visit(it)}
        instructions.add(Goto(loopLabel))

        instructions.add(Label(endLabel))
    }

    private fun visitFor(node:ForStatement){
        // Initializer
        node.init?.let{ visit(it) }

        val loopLabel = newLabel()
        val endLabel = newLabel()

        instructions.add(Label(loopLabel))

        // Condition
        node.condition?.let{
            val conditionTemp = visitExpression(it)
            instructions.add(IfGoto(conditionTemp,endLabel))
        }

        // Loop body
        node.body.forEach{ visit(it) }

        // Update
        node.update?.let {visitExpression(it)}

        instructions.add(Goto(loopLabel))
        instructions.add(Label(endLabel))
    }

    private fun visitAssignment(node:Assignment):String{
        val value = visitExpression(node.value)
        instructions.add(Assign(node.name, value))
        return value
    }

    private fun visitBinary(node:BinaryExpression): String{
        val left = visitExpression(node.left)
        val right = visitExpression(node.right)
        val result = newTemp()

        instructions.add(BinaryOp(result,left,node.op,right))
        return result
    }

    private fun visitUnary(node: UnaryExpression): String{
        val expr = visitExpression(node.expr)
        val result = newTemp()

        when(node.op){
            "-" -> instructions.add(BinaryOp(result,"0","-",expr))
            "!" -> {
                val zero = newTemp()
                instructions.add(Assign(zero,"0"))
                instructions.add(BinaryOp(result,zero,"==",expr))
            }
        }
        return result
    }

    private fun visitVariable(node:Variable):String{
        return symbol_table[node.name] ?: node.name
    }

    private fun visitLiteral(node:Literal): String{
        return node.value.toString()
    }

    private fun visitGrouping(node:Grouping):String{
        return visitExpression(node.expr)
    }

    private fun visitExpression(node:Expression):String{
        return when(node){
            is Literal -> node.value.toString()
            is Variable -> symbol_table[node.name] ?: node.name
            is Assignment -> visitAssignment(node)
            is BinaryExpression -> visitBinary(node)
            is UnaryExpression -> visitUnary(node)
            is Grouping -> visitExpression(node.expr)

            else ->{
                val temp = newTemp()
                instructions.add(Assign(temp,"0"))
                temp
            }
        }
    }
}