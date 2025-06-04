package unam.fi.compilers.team2.parser

sealed class ASTNode
data class FunctionDecl(val returnType: String, val name: String, val body: Block) : ASTNode()
data class ClassDecl(val name: String, val members: List<ASTNode>) : ASTNode()
data class VarDecl(val type: String, val name: String, val value: Expression?) : ASTNode()
