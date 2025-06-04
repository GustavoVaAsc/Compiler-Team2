package unam.fi.compilers.team2.parser

import androidx.core.app.RemoteInput.Source

class Grammar {
    private val grammar: List<Production>

    init {
        grammar = listOf(
            // Augmented start production
            Production(Nonterminal("S'"), listOf(Nonterminal("Source"))),

            // Source ::= Libs? TopDeclarations
            Production(Nonterminal("Source"), listOf(Nonterminal("Libs"), Nonterminal("TopDeclarations"))),

            // Libs ::= { import Id ; }*
            Production(Nonterminal("Libs"), listOf()), // epsilon
            Production(Nonterminal("Libs"), listOf(Nonterminal("Libs"), Terminal("import"), Nonterminal("Identifier"), Terminal(";"))),

            // TopDeclarations ::= { Declaration | Function | Class }*
            Production(Nonterminal("TopDeclarations"), listOf()),
            Production(Nonterminal("TopDeclarations"), listOf(Nonterminal("TopDeclarations"), Nonterminal("Declaration"))),
            Production(Nonterminal("TopDeclarations"), listOf(Nonterminal("TopDeclarations"), Nonterminal("Function"))),
            Production(Nonterminal("TopDeclarations"), listOf(Nonterminal("TopDeclarations"), Nonterminal("Class"))),

            // Declaration ::= Constant | TypeDecl | Variable
            Production(Nonterminal("Declaration"), listOf(Nonterminal("Constant"))),
            Production(Nonterminal("Declaration"), listOf(Nonterminal("TypeDecl"))),
            Production(Nonterminal("Declaration"), listOf(Nonterminal("Variable"))),

            // Constant ::= 'constant' Type Identifier '=' Expression ';'
            Production(Nonterminal("Constant"),
                listOf(Terminal("constant"), Nonterminal("Type"), Nonterminal("Identifier"), Terminal("="), Nonterminal("Expression"), Terminal(";"))
            ),

            // TypeDecl ::= 'type' Identifier ';' | 'type' Identifier '=' Type ';'
            Production(Nonterminal("TypeDecl"), listOf(Terminal("type"), Nonterminal("Identifier"), Terminal(";"))),
            Production(Nonterminal("TypeDecl"), listOf(Terminal("type"), Nonterminal("Identifier"), Terminal("="), Nonterminal("Type"), Terminal(";"))),

            // Variable ::= Type Identifier [= Expression]? ';'
            Production(Nonterminal("Variable"), listOf(Nonterminal("Type"), Nonterminal("Identifier"), Terminal(";"))),
            Production(Nonterminal("Variable"), listOf(Nonterminal("Type"), Nonterminal("Identifier"), Terminal("="), Nonterminal("Expression"), Terminal(";"))),

            // Function ::= Type 'function' Identifier '(' Parameters? ')' Block
            Production(Nonterminal("Function"),
                listOf(Nonterminal("Type"), Terminal("function"), Nonterminal("Identifier"), Terminal("("), Nonterminal("ParametersOpt"), Terminal(")"), Nonterminal("Block"))
            ),

            // ParametersOpt ::= empty | Parameters
            Production(Nonterminal("ParametersOpt"), listOf()),
            Production(Nonterminal("ParametersOpt"), listOf(Nonterminal("Parameters"))),

            // Parameters ::= Parameter {',' Parameter}*
            Production(Nonterminal("Parameters"), listOf(Nonterminal("Parameter"))),
            Production(Nonterminal("Parameters"), listOf(Nonterminal("Parameters"), Terminal(","), Nonterminal("Parameter"))),

            // Parameter ::= Type Identifier
            Production(Nonterminal("Parameter"), listOf(Nonterminal("Type"), Nonterminal("Identifier"))),

            // Class ::= 'class' Identifier ClassBlock
            Production(Nonterminal("Class"), listOf(Terminal("class"), Nonterminal("Identifier"), Nonterminal("ClassBlock"))),

            // ClassBlock ::= '{' ClassMember* '}'
            Production(Nonterminal("ClassBlock"), listOf(Terminal("{"), Nonterminal("ClassMembers"), Terminal("}"))),
            Production(Nonterminal("ClassMembers"), listOf()),
            Production(Nonterminal("ClassMembers"), listOf(Nonterminal("ClassMembers"), Nonterminal("ClassMember"))),

            // ClassMember ::= Variable ';' | Function
            Production(Nonterminal("ClassMember"), listOf(Nonterminal("Variable"))),
            Production(Nonterminal("ClassMember"), listOf(Nonterminal("Function"))),

            // Type ::= Datatype | Identifier | Type '[' ']'
            Production(Nonterminal("Type"), listOf(Nonterminal("Datatype"))),
            Production(Nonterminal("Type"), listOf(Nonterminal("Identifier"))),
            Production(Nonterminal("Type"), listOf(Nonterminal("Type"), Terminal("["), Terminal("]"))),

            // Datatype ::= (all your datatypes as terminals)
            Production(Nonterminal("Datatype"), listOf(Terminal("bool"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("char"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("class"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("enumerate"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("float32"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("float64"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("int8"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("int16"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("int32"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("int64"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("string"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("unsigned"))),
            Production(Nonterminal("Datatype"), listOf(Terminal("void"))),

            // Block ::= '{' Statements '}'
            Production(Nonterminal("Block"), listOf(Terminal("{"), Nonterminal("Statements"), Terminal("}"))),

            // Statements ::= empty | Statements Statement
            Production(Nonterminal("Statements"), listOf()),
            Production(Nonterminal("Statements"), listOf(Nonterminal("Statements"), Nonterminal("Statement"))),

            // Statement ::= Variable ';' | Assignment ';' | FunctionCall ';' | ControlFlow | Return ';' | Block
            Production(Nonterminal("Statement"), listOf(Nonterminal("Variable"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("Assignment"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("FunctionCall"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("ControlFlow"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("Return"))),
            Production(Nonterminal("Statement"), listOf(Nonterminal("Block"))),

            // Assignment ::= Identifier '=' Expression
            Production(Nonterminal("Assignment"), listOf(Nonterminal("Identifier"), Terminal("="), Nonterminal("Expression"))),

            // Return ::= 'return' Expression?
            Production(Nonterminal("Return"), listOf(Terminal("return"))),
            Production(Nonterminal("Return"), listOf(Terminal("return"), Nonterminal("Expression"))),

            // FunctionCall ::= Identifier '(' ArgumentsOpt ')'
            Production(Nonterminal("FunctionCall"), listOf(Nonterminal("Identifier"), Terminal("("), Nonterminal("ArgumentsOpt"), Terminal(")"))),

            // ArgumentsOpt ::= empty | Arguments
            Production(Nonterminal("ArgumentsOpt"), listOf()),
            Production(Nonterminal("ArgumentsOpt"), listOf(Nonterminal("Arguments"))),

            // Arguments ::= Expression { ',' Expression }*
            Production(Nonterminal("Arguments"), listOf(Nonterminal("Expression"))),
            Production(Nonterminal("Arguments"), listOf(Nonterminal("Arguments"), Terminal(","), Nonterminal("Expression"))),

            // ControlFlow ::= IfStatement | WhileStatement
            Production(Nonterminal("ControlFlow"), listOf(Nonterminal("IfStatement"))),
            Production(Nonterminal("ControlFlow"), listOf(Nonterminal("WhileStatement"))),

            // IfStatement ::= 'if' '(' Expression ')' Block ElsePart?
            Production(Nonterminal("IfStatement"),
                listOf(Terminal("if"), Terminal("("), Nonterminal("Expression"), Terminal(")"), Nonterminal("Block"), Nonterminal("ElsePartOpt"))
            ),

            // ElsePartOpt ::= empty | ElsePart
            Production(Nonterminal("ElsePartOpt"), listOf()),
            Production(Nonterminal("ElsePartOpt"), listOf(Terminal("else"), Nonterminal("Block"))),

            // WhileStatement ::= 'while' '(' Expression ')' Block
            Production(Nonterminal("WhileStatement"),
                listOf(Terminal("while"), Terminal("("), Nonterminal("Expression"), Terminal(")"), Nonterminal("Block"))
            ),

            // Expression ::= (handle operators and precedence)
            // For simplicity, a minimal expression grammar:

            // Expression ::= Expression Operator Expression | '(' Expression ')' | Identifier | Constant | Literal | Boolean
            Production(Nonterminal("Expression"), listOf(Nonterminal("Expression"), Terminal("Operator"), Nonterminal("Expression"))), // Note: You'll need operator precedence handling here
            Production(Nonterminal("Expression"), listOf(Terminal("("), Nonterminal("Expression"), Terminal(")"))),
            Production(Nonterminal("Expression"), listOf(Nonterminal("Identifier"))),
            Production(Nonterminal("Expression"), listOf(Nonterminal("Constant"))),
            Production(Nonterminal("Expression"), listOf(Nonterminal("Literal"))),
            Production(Nonterminal("Expression"), listOf(Nonterminal("Boolean"))),

            // Identifier ::= Terminal category "Identifier"
            Production(Nonterminal("Identifier"), listOf(Terminal("Identifier"))),

            // Constant ::= Terminal category "Constant"
            Production(Nonterminal("Constant"), listOf(Terminal("Constant"))),

            // Literal ::= Terminal category "Literal"
            Production(Nonterminal("Literal"), listOf(Terminal("Literal"))),

            // Boolean ::= Terminal category "Boolean"
            Production(Nonterminal("Boolean"), listOf(Terminal("Boolean"))),

            // Operator ::= all your operators as terminals or a generic Operator terminal
            Production(Nonterminal("Operator"), listOf(Terminal("Operator"))),

            // Relation ::= relational operators, e.g. ==, !=, <, >, etc.
            Production(Nonterminal("Relation"), listOf(Terminal("Relation"))),

            // Punctuation ::= punctuation terminals like ;, {, }, (, ), etc.
            Production(Nonterminal("Punctuation"), listOf(Terminal("Punctuation")))
        )

    }

    fun getGrammar(): List<Production> = grammar
}
