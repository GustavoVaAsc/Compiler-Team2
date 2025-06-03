package unam.fi.compilers.team2.parser

import androidx.core.app.RemoteInput.Source

class Grammar {
    private lateinit var grammar: List<Production>;
    init{
        grammar = listOf(
            // Augmented start production
            Production(Nonterminal("S'"), listOf(Nonterminal("Source"))),

            // Source
            Production(Nonterminal("Source"), listOf(Nonterminal("Libs"), Nonterminal("TopDeclarations"))),

            // Libs
            Production(Nonterminal("Libs"), listOf()), // Epsilon production
            Production(Nonterminal("Libs"), listOf(Nonterminal("Libs"), Terminal("import"), Nonterminal("ID"), Terminal(";"))),

            // Top Declarations
            Production(Nonterminal("TopDeclarations"), listOf()),
            Production(Nonterminal("TopDeclarations"), listOf(Nonterminal("TopDeclarations"), Nonterminal("Declaration"))),
            Production(Nonterminal("TopDeclarations"), listOf(Nonterminal("TopDeclarations"),Nonterminal("Function"))),
            Production(Nonterminal("TopDeclarations"), listOf(Nonterminal("TopDeclarations"), Nonterminal("Class"))),


            // Declaration
            Production(Nonterminal("Declaration"), listOf(Nonterminal("Constant"))),
            Production(Nonterminal("Declaration"), listOf(Nonterminal("TypeDecl"))),
            Production(Nonterminal("Declaration"), listOf(Nonterminal("Variable"))),

            // Function
            Production(Nonterminal("Function"), listOf(Nonterminal("Type"), Terminal("function"), Nonterminal("ID"), Terminal("("), Nonterminal("Parameters"), Terminal(")"),Nonterminal("Block"))),
            // Parameters
            Production(Nonterminal("Parameters"), listOf()), // No parameters
            Production(Nonterminal("Parameters"), listOf(Nonterminal("ParameterList"))),
            // List of parameters
            Production(Nonterminal("ParameterList"), listOf(Nonterminal("Parameter"))),
            Production(Nonterminal("ParameterList"), listOf(Nonterminal("ParameterList"), Terminal(","), Nonterminal("Parameter"))),
            // Single parameter
            Production(Nonterminal("Parameter"), listOf(Nonterminal("Type"), Nonterminal("ID"))),

            // TODO: Continue the grammar mapping
            // Class
            Production(Nonterminal("Class"), listOf(Terminal("class"), Nonterminal("ID"), Terminal("("), Nonterminal("Parameters"), Terminal(")"), Nonterminal("ClassBlock"))),
            Production(Nonterminal("ClassBlock"), listOf(Terminal("{"), Nonterminal("ClassMembers"), Terminal("}"))),
            Production(Nonterminal("ClassMembers"), listOf()),
            Production(Nonterminal("ClassMembers"), listOf(Nonterminal("ClassMembers"), Nonterminal("ClassMember"))),
            Production(Nonterminal("ClassMember"), listOf(Nonterminal("VarDecl"))),
            Production(Nonterminal("ClassMember"), listOf(Nonterminal("Function"))),

            // Type
            Production(Nonterminal("Type"), listOf(Terminal("Int"))),
            Production(Nonterminal("Type"), listOf(Terminal("Float"))),
            Production(Nonterminal("Type"), listOf(Terminal("Boolean"))),
            Production(Nonterminal("Type"), listOf(Terminal("String"))),
            Production(Nonterminal("Type"), listOf(Terminal("Unit"))),
            Production(Nonterminal("Type"), listOf(Nonterminal("ID"))),

            // Block and Statements
            Production(Nonterminal("Block"), listOf(Terminal("{"), Nonterminal("Statements"), Terminal("}"))),
            Production(Nonterminal("Statements"), listOf()),
            Production(Nonterminal("Statements"), listOf(Nonterminal("Statements"), Nonterminal("Statement"))),

            // Statements
            Production(Nonterminal("Statement"), listOf(Nonterminal("VarDecl"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("Assignment"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("FunctionCall"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("ControlFlow"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("ReturnStatement"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("Block"))),

            // Assignment
            Production(Nonterminal("Assignment"), listOf(Nonterminal("ID"), Terminal("="), Nonterminal("Expression"))),

            // Return
            Production(Nonterminal("ReturnStatement"), listOf(Terminal("return"))),
            Production(Nonterminal("ReturnStatement"), listOf(Terminal("return"), Nonterminal("Expression"))),

            // Function Call
            Production(Nonterminal("FunctionCall"), listOf(Nonterminal("ID"), Terminal("("), Nonterminal("Arguments"), Terminal(")"))),
            Production(Nonterminal("Arguments"), listOf()),
            Production(Nonterminal("Arguments"), listOf(Nonterminal("ExpressionList"))),
            Production(Nonterminal("ExpressionList"), listOf(Nonterminal("Expression"))),
            Production(Nonterminal("ExpressionList"), listOf(Nonterminal("ExpressionList"), Terminal(","), Nonterminal("Expression"))),

            // Control Flow
            Production(Nonterminal("ControlFlow"), listOf(Nonterminal("IfStatement"))),
            Production(Nonterminal("ControlFlow"), listOf(Nonterminal("WhileStatement"))),
            Production(Nonterminal("IfStatement"), listOf(Terminal("if"), Terminal("("), Nonterminal("Expression"), Terminal(")"), Nonterminal("Block"), Nonterminal("ElsePart"))),
            Production(Nonterminal("ElsePart"), listOf()),
            Production(Nonterminal("ElsePart"), listOf(Terminal("else"), Nonterminal("IfStatement"))),
            Production(Nonterminal("ElsePart"), listOf(Terminal("else"), Nonterminal("Block"))),
            Production(Nonterminal("WhileStatement"), listOf(Terminal("while"), Terminal("("), Nonterminal("Expression"), Terminal(")"), Nonterminal("Block"))),

            // Expressions
            Production(Nonterminal("Expression"), listOf(Nonterminal("Literal"))),
            Production(Nonterminal("Expression"), listOf(Nonterminal("ID"))),
            Production(Nonterminal("Expression"), listOf(Nonterminal("FunctionCall"))),
            Production(Nonterminal("Expression"), listOf(Nonterminal("UnaryOp"), Nonterminal("Expression"))),
            Production(Nonterminal("Expression"), listOf(Nonterminal("Expression"), Nonterminal("BinaryOp"), Nonterminal("Expression"))),
            Production(Nonterminal("Expression"), listOf(Terminal("("), Nonterminal("Expression"), Terminal(")"))),

            // Operators
            Production(Nonterminal("UnaryOp"), listOf(Terminal("-"))),
            Production(Nonterminal("UnaryOp"), listOf(Terminal("!"))),

            Production(Nonterminal("BinaryOp"), listOf(Terminal("+"))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("-"))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("*"))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("/"))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("%"))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("=="))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("!="))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("<"))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal(">"))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("<="))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal(">="))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("&&"))),
            Production(Nonterminal("BinaryOp"), listOf(Terminal("||"))),

            // Literals
            Production(Nonterminal("Literal"), listOf(Terminal("INTEGER"))),
            Production(Nonterminal("Literal"), listOf(Terminal("FLOAT"))),
            Production(Nonterminal("Literal"), listOf(Terminal("BOOLEAN"))),
            Production(Nonterminal("Literal"), listOf(Terminal("STRING"))),
            Production(Nonterminal("Literal"), listOf(Terminal("null")))
        )
    }

    public fun getGrammar():List<Production> = this.grammar
}
