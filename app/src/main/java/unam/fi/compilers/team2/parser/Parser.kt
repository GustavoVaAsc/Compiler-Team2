package unam.fi.compilers.team2.parser

/*
---- GRAMMAR OF K* (EBNF) ----

<grammar>
    <Source> ::= <Libs> <TopDeclarations>

    <Libs> ::= { import <Id> ; }

    <TopDeclarations> ::= { <Declaration> | <Function> | <Class> }

    <Declaration> ::= <Constant> | <TypeDecl> | <Variable>

    <Function> ::= <Type> function <Id> ( <Parameters>? ) <Block>

    <Class> ::= class <Id> <ClassBlock>

    <ClassBlock> ::= { <ClassMember>* }

    <ClassMember> ::= <Variable> ; | <Function>

    <TypeDecl> ::= type <Id> ; | type <Id> = <Type> ;

    <Type> ::= <PrimitiveType> | <Id> | <Type> [ ]  // Basic types + arrays

    <PrimitiveType> ::= int | float | bool | string | void

    <Constant> ::= const <Type> <Id> = <Expression> ;

    <Variable> ::= <Type> <Id> [ = <Expression> ]? ;

    <Parameters> ::= <Type> <Id> { , <Type> <Id> }

    <Block> ::= { <Statement>* }

    <Statement> ::= <Variable> ;
                  | <Assignment> ;
                  | <FunctionCall> ;
                  | <ControlFlow>
                  | <Return> ;
                  | <Block>

    <Assignment> ::= <Id> = <Expression>

    <FunctionCall> ::= <Id> ( <Arguments>? )

    <Arguments> ::= <Expression> { , <Expression> }

    <ControlFlow> ::= <IfStatement> | <WhileStatement>

    <IfStatement> ::= if ( <Expression> ) <Block> <ElsePart>?

    <ElsePart> ::= else <IfStatement>
             | else <Block>


    <WhileStatement> ::= while ( <Expression> ) <Block>

    <Return> ::= return [ <Expression> ]?

    <Expression> ::= <Literal>
                   | <Id>
                   | <FunctionCall>
                   | <UnaryOp> <Expression>
                   | <Expression> <BinaryOp> <Expression>
                   | ( <Expression> )

    <UnaryOp> ::= - | !

    <BinaryOp> ::= + | - | * | / | % | == | != | < | > | <= | >= | && | ||

    <Literal> ::= <Integer> | <Float> | <Boolean> | <String> | null

    <Integer> ::= [0-9]+
    <Float> ::= [0-9]+ \. [0-9]*
    <Boolean> ::= true | false
    <String> ::= " ( [^"\n] | \\" )* "
</grammar>

*/


// This class implements LALR(1) parser
class Parser {
    private val states = mutableListOf<State>()
    private val goto_table = mutableMapOf<Pair<Int,Nonterminal>,Int>()
    private val action_table = mutableMapOf<Pair<Int,Terminal>,Action>()
    private val grammar = Grammar()

    // Closure for LR(1) items
    private fun closure(items:Set<Item>):Set<Item>{
        val newItems = items.toMutableSet()
        var changed: Boolean = true

        while(changed){
            changed = false
            items.toList().forEach{ item ->
                val nextSymbol = item.nextSymbol() as? Nonterminal
                nextSymbol?.let {nt ->
                    val g = grammar.getGrammar()
                    g.filter{it.symbol == nt}.forEach{prod ->
                        val lookaheads = computeLookaheads(item,prod)
                        lookaheads.forEach{la ->
                            val newItem = Item(prod, 0, la)
                            if(newItems.add(newItem)) changed = true
                        }
                    }
                }
            }
        }
        return newItems
    }

    private fun computeLookaheads(item:Item, prod:Production): Set<Terminal>{
        // TODO: Compute FIRST set here
        return setOf(/*eof*/)
    }

    // Compute GOTO function
    private fun goto(items:Set<Item>, symbol:Symbol): Set<Item>{
        val newItems = mutableSetOf<Item>()
        items.forEach{item ->
            if(item.nextSymbol() == symbol){
                newItems.add(Item(item.production, item.lookahead_position+1, item.lookahead))
            }
        }
        return closure(newItems)
    }

    // Build canonical collection (LALR Automata)
    fun buildStates(){
        //val startItem = Item(startProduction, 0, eof)
    }
}