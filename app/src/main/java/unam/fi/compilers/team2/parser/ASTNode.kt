package unam.fi.compilers.team2.parser
// Base interfaces
interface ASTNode {
    val line: Int
    val column: Int
    val production: Production?
}

// Expressions
sealed interface ExpressionNode : ASTNode

data class BinaryOpNode(
    val left: ExpressionNode,
    val operator: String,
    val right: ExpressionNode,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ExpressionNode

data class UnaryOpNode(
    val operator: String,
    val operand: ExpressionNode,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ExpressionNode

// Statements
sealed interface StatementNode : ASTNode

data class BlockNode(
    val statements: List<StatementNode>,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : StatementNode

data class IfNode(
    val condition: ExpressionNode,
    val thenBranch: BlockNode,
    val elseBranch: BlockNode?,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : StatementNode

data class WhileNode(
    val condition: ExpressionNode,
    val body: BlockNode,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : StatementNode

// Declarations
data class DeclarationNode(
    val type: TypeNode,
    val name: IdentifierNode,
    val value: ExpressionNode?,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : StatementNode

data class AssignmentNode(
    val target: IdentifierNode,
    val value: ExpressionNode,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : StatementNode

// Functions
data class FunctionNode(
    val returnType: TypeNode,
    val name: IdentifierNode,
    val parameters: ParametersNode?,
    val body: BlockNode,
    override val production: Production? = null,
    override val line: Int = name.line,
    override val column: Int = name.column
) : ASTNode

data class ParametersNode(
    val parameters: List<ParameterNode>,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ASTNode

data class ParameterNode(
    val type: TypeNode,
    val name: IdentifierNode,
    override val line: Int = name.line,
    override val column: Int = name.column,
    override val production: Production? = null
) : ASTNode

// Classes
data class ClassNode(
    val name: IdentifierNode,
    val members: List<ClassMemberNode>,
    override val production: Production? = null,
    override val line: Int = name.line,
    override val column: Int = name.column
) : ASTNode

data class ClassBlockNode(
    val members: List<ClassMemberNode>,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ASTNode

sealed interface ClassMemberNode : ASTNode

data class ClassFieldNode(
    val type: TypeNode,
    val name: IdentifierNode,
    override val line: Int = name.line,
    override val column: Int = name.column,
    override val production: Production? = null
) : ClassMemberNode

data class ClassMethodNode(
    val function: FunctionNode,
    override val production: Production? = null,
    override val line: Int = function.line,
    override val column: Int = function.column
) : ClassMemberNode

// Types
data class TypeNode(
    val name: String,
    val isArray: Boolean = false,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ASTNode

data class TypeAliasNode(
    val name: IdentifierNode,
    val aliasType: TypeNode,
    override val production: Production? = null,
    override val line: Int = name.line,
    override val column: Int = name.column
) : ASTNode

// Literals
data class IdentifierNode(
    val name: String,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ExpressionNode

data class IntegerNode(
    val value: Int,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ExpressionNode

data class FloatNode(
    val value: Float,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ExpressionNode

data class StringNode(
    val value: String,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ExpressionNode

data class BooleanNode(
    val value: Boolean,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ExpressionNode

object NullNode : ExpressionNode {
    override val line: Int = -1
    override val column: Int = -1
    override val production: Production? = null
}

// Utility nodes
data class TokenNode(
    val terminal: Terminal,
    val value: String,
    override val line: Int,
    override val column: Int,
    override val production: Production? = null
) : ASTNode

data class ReturnNode(
    val expression: ASTNode?,
    override val line: Int,
    override val column: Int,
    override val production: Production
) : ASTNode
