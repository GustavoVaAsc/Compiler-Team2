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

            //Class
            // TODO: Continue the grammar mapping
        )
    }

    public fun getGrammar():List<Production> = this.grammar
}