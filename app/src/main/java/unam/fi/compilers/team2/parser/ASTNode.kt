package unam.fi.compilers.team2.parser

interface ASTNode

data class IdentifierNode(val name:String):ASTNode
data class IntegerNode(val value:Int):ASTNode
data class FloatNode(val value:Float):ASTNode
data class StringNode(val value:String):ASTNode
data class TypeNode(val name:String): ASTNode // Pending to check
data class ParametersNode(val name:String): ASTNode // Pending to check
data class BlockNode(val name:String): ASTNode
data class BooleanNode(val value:Boolean): ASTNode
data class TokenNode(val terminal: Terminal, val name:String): ASTNode

data class FunctionNode(
    val returnType: TypeNode,
    val name:IdentifierNode,
    val parameters: ParametersNode?,
    val body: BlockNode
): ASTNode