package unam.fi.compilers.team2.parser

class Grammar {
    private val grammar: List<Production>

    init {
        grammar = listOf(
            // Augmented start production
            Production(Nonterminal("S'"), listOf(Nonterminal("Source"))),

            // New unified source structure
            Production(Nonterminal("Source"), listOf(Nonterminal("SourceElements"))),
            Production(Nonterminal("SourceElements"), listOf(Nonterminal("SourceElement"), Nonterminal("SourceElements"))),
            Production(Nonterminal("SourceElements"), listOf()),  // ε production
            Production(Nonterminal("SourceElement"), listOf(Terminal("import"), Nonterminal("Id"), Terminal(";"))),
            Production(Nonterminal("SourceElement"), listOf(Nonterminal("Declaration"))),
            Production(Nonterminal("SourceElement"), listOf(Nonterminal("Function"))),
            Production(Nonterminal("SourceElement"), listOf(Nonterminal("Class"))),

            // Declaration ::= Constant | TypeDecl | Variable
            Production(Nonterminal("Declaration"), listOf(Nonterminal("Constant"))),
            Production(Nonterminal("Declaration"), listOf(Nonterminal("TypeDecl"))),
            Production(Nonterminal("Declaration"), listOf(Nonterminal("Variable"))),

            // Fixed Constant production (changed 'constant' to 'const')
            Production(Nonterminal("Constant"),
                listOf(Terminal("const"), Nonterminal("Type"), Terminal("ID"), Terminal("="), Nonterminal("Expression"), Terminal(";"))
            ),

            // TypeDecl
            Production(Nonterminal("TypeDecl"), listOf(Terminal("type"), Terminal("ID"), Terminal(";"))),
            Production(Nonterminal("TypeDecl"), listOf(Terminal("type"), Terminal("ID"), Terminal("="), Nonterminal("Type"), Terminal(";"))),

            // Variable
            Production(Nonterminal("Variable"), listOf(Nonterminal("Type"), Terminal("ID"), Terminal(";"))),
            Production(Nonterminal("Variable"), listOf(Nonterminal("Type"), Terminal("ID"), Terminal("="), Nonterminal("Expression"), Terminal(";"))),

            // Function
            Production(Nonterminal("Function"),
                listOf(Terminal("function"), Nonterminal("Type"), Terminal("ID"), Terminal("("), Nonterminal("ParametersOpt"), Terminal(")"), Nonterminal("Block"))
            ),

            // ParametersOpt
            Production(Nonterminal("ParametersOpt"), listOf()),
            Production(Nonterminal("ParametersOpt"), listOf(Nonterminal("Parameters"))),

            // Parameters
            Production(Nonterminal("Parameters"), listOf(Nonterminal("Parameter"))),
            Production(Nonterminal("Parameters"), listOf(Nonterminal("Parameters"), Terminal(","), Nonterminal("Parameter"))),

            // Parameter
            Production(Nonterminal("Parameter"), listOf(Nonterminal("Type"), Terminal("ID"))),

            // Class
            Production(Nonterminal("Class"), listOf(Terminal("class"), Terminal("ID"), Nonterminal("ClassBlock"))),

            // ClassBlock
            Production(Nonterminal("ClassBlock"), listOf(Terminal("{"), Nonterminal("ClassMembers"), Terminal("}"))),
            Production(Nonterminal("ClassMembers"), listOf()),
            Production(Nonterminal("ClassMembers"), listOf(Nonterminal("ClassMembers"), Nonterminal("ClassMember"))),

            // ClassMember
            Production(Nonterminal("ClassMember"), listOf(Nonterminal("Variable"))),
            Production(Nonterminal("ClassMember"), listOf(Nonterminal("Function"))),

            // Type
            Production(Nonterminal("Type"), listOf(Nonterminal("SimpleType"), Nonterminal("ArraySuffixOpt"))),
            Production(Nonterminal("ArraySuffixOpt"), listOf()),
            Production(Nonterminal("ArraySuffixOpt"), listOf(Terminal("["), Terminal("]"), Nonterminal("ArraySuffixOpt"))),
            Production(Nonterminal("SimpleType"), listOf(Nonterminal("PrimitiveType"))),
            Production(Nonterminal("SimpleType"), listOf(Terminal("ID"))),

            // PrimitiveType
            Production(Nonterminal("PrimitiveType"), listOf(Terminal("bool"))),
            Production(Nonterminal("PrimitiveType"), listOf(Terminal("int"))),
            Production(Nonterminal("PrimitiveType"), listOf(Terminal("float"))),
            Production(Nonterminal("PrimitiveType"), listOf(Terminal("string"))),
            Production(Nonterminal("PrimitiveType"), listOf(Terminal("void"))),

            // Block
            Production(Nonterminal("Block"), listOf(Terminal("{"), Nonterminal("Statements"), Terminal("}"))),

            // Statements
            Production(Nonterminal("Statements"), listOf()),
            Production(Nonterminal("Statements"), listOf(Nonterminal("Statements"), Nonterminal("Statement"))),

            // Statement
            Production(Nonterminal("Statement"), listOf(Nonterminal("Variable"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("Assignment"), Terminal(";"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("FunctionCall"), Terminal(";"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("ControlFlow"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("Return"), Terminal(";"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("Block"))),

            // Assignment
            Production(Nonterminal("Assignment"), listOf(Terminal("ID"), Terminal("="), Nonterminal("Expression"))),

            // Return
            Production(Nonterminal("Return"), listOf(Terminal("return"))),
            Production(Nonterminal("Return"), listOf(Terminal("return"), Nonterminal("Expression"))),

            // FunctionCall
            Production(Nonterminal("FunctionCall"), listOf(Terminal("ID"), Terminal("("), Nonterminal("ArgumentsOpt"), Terminal(")"))),

            // ArgumentsOpt
            Production(Nonterminal("ArgumentsOpt"), listOf()),
            Production(Nonterminal("ArgumentsOpt"), listOf(Nonterminal("Arguments"))),

            // Arguments
            Production(Nonterminal("Arguments"), listOf(Nonterminal("Expression"))),
            Production(Nonterminal("Arguments"), listOf(Nonterminal("Arguments"), Terminal(","), Nonterminal("Expression"))),

            // ControlFlow
            Production(Nonterminal("ControlFlow"), listOf(Nonterminal("IfStatement"))),
            Production(Nonterminal("ControlFlow"), listOf(Nonterminal("WhileStatement"))),

            // IfStatement
            Production(Nonterminal("IfStatement"),
                listOf(Terminal("if"), Terminal("("), Nonterminal("Expression"), Terminal(")"), Nonterminal("Block"), Nonterminal("ElsePartOpt"))
            ),

            // ElsePartOpt
            Production(Nonterminal("ElsePartOpt"), listOf()),
            Production(Nonterminal("ElsePartOpt"), listOf(Terminal("else"), Nonterminal("Block"))),

            // WhileStatement
            Production(Nonterminal("WhileStatement"),
                listOf(Terminal("while"), Terminal("("), Nonterminal("Expression"), Terminal(")"), Nonterminal("Block"))
            ),

            // Expression hierarchy
            Production(Nonterminal("Expression"), listOf(Nonterminal("ExprOr"))),
            Production(Nonterminal("ExprOr"), listOf(Nonterminal("ExprAnd"))),
            Production(Nonterminal("ExprOr"), listOf(Nonterminal("ExprOr"), Terminal("||"), Nonterminal("ExprAnd"))),

            Production(Nonterminal("ExprAnd"), listOf(Nonterminal("ExprEquality"))),
            Production(Nonterminal("ExprAnd"), listOf(Nonterminal("ExprAnd"), Terminal("&&"), Nonterminal("ExprEquality"))),

            Production(Nonterminal("ExprEquality"), listOf(Nonterminal("ExprComparison"))),
            Production(Nonterminal("ExprEquality"), listOf(Nonterminal("ExprEquality"), Terminal("=="), Nonterminal("ExprComparison"))),
            Production(Nonterminal("ExprEquality"), listOf(Nonterminal("ExprEquality"), Terminal("!="), Nonterminal("ExprComparison"))),

            Production(Nonterminal("ExprComparison"), listOf(Nonterminal("ExprTerm"))),
            Production(Nonterminal("ExprComparison"), listOf(Nonterminal("ExprComparison"), Terminal("<"), Nonterminal("ExprTerm"))),
            Production(Nonterminal("ExprComparison"), listOf(Nonterminal("ExprComparison"), Terminal("<="), Nonterminal("ExprTerm"))),
            Production(Nonterminal("ExprComparison"), listOf(Nonterminal("ExprComparison"), Terminal(">"), Nonterminal("ExprTerm"))),
            Production(Nonterminal("ExprComparison"), listOf(Nonterminal("ExprComparison"), Terminal(">="), Nonterminal("ExprTerm"))),

            Production(Nonterminal("ExprTerm"), listOf(Nonterminal("ExprFactor"))),
            Production(Nonterminal("ExprTerm"), listOf(Nonterminal("ExprTerm"), Terminal("+"), Nonterminal("ExprFactor"))),
            Production(Nonterminal("ExprTerm"), listOf(Nonterminal("ExprTerm"), Terminal("-"), Nonterminal("ExprFactor"))),

            Production(Nonterminal("ExprFactor"), listOf(Nonterminal("ExprUnary"))),
            Production(Nonterminal("ExprFactor"), listOf(Nonterminal("ExprFactor"), Terminal("*"), Nonterminal("ExprUnary"))),
            Production(Nonterminal("ExprFactor"), listOf(Nonterminal("ExprFactor"), Terminal("/"), Nonterminal("ExprUnary"))),
            Production(Nonterminal("ExprFactor"), listOf(Nonterminal("ExprFactor"), Terminal("%"), Nonterminal("ExprUnary"))),

            Production(Nonterminal("ExprUnary"), listOf(Nonterminal("ExprPrimary"))),
            Production(Nonterminal("ExprUnary"), listOf(Terminal("!"), Nonterminal("ExprUnary"))),
            Production(Nonterminal("ExprUnary"), listOf(Terminal("-"), Nonterminal("ExprUnary"))),

            // Fixed primary expressions with proper token types
            Production(Nonterminal("ExprPrimary"), listOf(Terminal("ID"))),
            Production(Nonterminal("ExprPrimary"), listOf(Terminal("INTEGER"))),
            Production(Nonterminal("ExprPrimary"), listOf(Terminal("FLOAT"))),
            Production(Nonterminal("ExprPrimary"), listOf(Terminal("STRING"))),
            Production(Nonterminal("ExprPrimary"), listOf(Terminal("true"))),
            Production(Nonterminal("ExprPrimary"), listOf(Terminal("false"))),
            Production(Nonterminal("ExprPrimary"), listOf(Terminal("null"))),
            Production(Nonterminal("ExprPrimary"), listOf(Terminal("("), Nonterminal("Expression"), Terminal(")"))),
            Production(Nonterminal("ExprPrimary"), listOf(Nonterminal("FunctionCall"))),

            Production(Nonterminal("Expression"), listOf(Nonterminal("ExprPrimary")))

        )
    }

    fun getGrammar(): List<Production> = grammar
}