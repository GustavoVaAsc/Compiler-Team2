package unam.fi.compilers.team2.parser

/*
---- GRAMMAR OF K* (EBNF) ----

<Source> ::= [ <Libs> ] <TopDeclarations>

<Libs> ::= { "import" <Id> ";" }

<TopDeclarations> ::= { <Declaration> | <Function> | <Class> }

<Declaration> ::= <Constant> | <TypeDecl> | <Variable>

<Function> ::= <Type> "function" <Id> "(" [ <Parameters> ] ")" <Block>

<Class> ::= "class" <Id> <ClassBlock>

<ClassBlock> ::= "{" { <ClassMember> } "}"

<ClassMember> ::= <Variable> ";" | <Function>

<TypeDecl> ::= "type" <Id> ";" | "type" <Id> "=" <Type> ";"

<Type> ::= <PrimitiveType> | <Id> | <Type> "[" "]"  (* basic types and arrays *)

<PrimitiveType> ::= "int" | "float" | "bool" | "string" | "void"

<Constant> ::= "const" <Type> <Id> "=" <Expression> ";"

<Variable> ::= <Type> <Id> [ "=" <Expression> ] ";"

<Parameters> ::= <Type> <Id> { "," <Type> <Id> }

<Block> ::= "{" { <Statement> } "}"

<Statement> ::= <Variable> ";"
              | <Assignment> ";"
              | <FunctionCall> ";"
              | <ControlFlow>
              | <Return> ";"
              | <Block>

<Assignment> ::= <Id> "=" <Expression>

<FunctionCall> ::= <Id> "(" [ <Arguments> ] ")"

<Arguments> ::= <Expression> { "," <Expression> }

<ControlFlow> ::= <IfStatement> | <WhileStatement>

<IfStatement> ::= "if" "(" <Expression> ")" <Block> [ <ElsePart> ]

<ElsePart> ::= "else" <IfStatement>
             | "else" <Block>

<WhileStatement> ::= "while" "(" <Expression> ")" <Block>

<Return> ::= "return" [ <Expression> ]

<Expression> ::= <Literal>
               | <Id>
               | <FunctionCall>
               | <UnaryOp> <Expression>
               | <Expression> <BinaryOp> <Expression>
               | "(" <Expression> ")"

<UnaryOp> ::= "-" | "!"

<BinaryOp> ::= "+" | "-" | "*" | "/" | "%" | "==" | "!=" | "<" | ">" | "<=" | ">=" | "&&" | "||"

<Literal> ::= <Integer> | <Float> | <Boolean> | <String> | "null"

<Integer> ::= digit { digit }

<Float> ::= digit { digit } "." digit { digit }

<Boolean> ::= "true" | "false"

<String> ::= "\"" { character | escaped_quote } "\""

<Id> ::= letter { letter | digit | "_" }

(*

Where:

- digit = "0" | "1" | ... | "9"
- letter = "a" | "b" | ... | "z" | "A" | "B" | ... | "Z"
- character = any character except " or newline
- escaped_quote = "\\\""

*)


*/


// This class implements LALR(1) parser
class Parser {
    private val states = mutableListOf<State>()
    public val goto_table = mutableMapOf<Pair<Int,Nonterminal>,Int>()
    public val action_table = mutableMapOf<Pair<Int,Terminal>,Action>()
    private val grammar = Grammar()
    private val EOF = Terminal("$")
    private val START_PRODUCTION = Production(Nonterminal("S'"), listOf(Nonterminal("Source")))
    private val nullable:Set<Nonterminal> = computeNullableSet()
    private val firstSet: Map<Symbol,Set<Terminal>> = computeFirstSets()
    private val precedence_map = mapOf(
        "*" to 7, "/" to 7, "%" to 7,
        "+" to 6, "-" to 6,
        "<" to 5, ">" to 5, "<=" to 5, ">=" to 5,
        "==" to 4, "!=" to 4,
        "&&" to 3,
        "||" to 2
    )
    // Nullable set (Non terminals that could derive to Epsilon)

    private fun computeNullableSet():Set <Nonterminal> {
        val nullable = mutableSetOf<Nonterminal>()
        var changed: Boolean

        do{
            changed = false
            for(prod in grammar.getGrammar()){
                if(prod.expressions.isEmpty() || prod.expressions.all{it is Nonterminal && it in nullable}){
                    if(prod.symbol !in nullable){
                        nullable.add(prod.symbol)
                        changed = true
                    }
                }
            }
        }while(changed)

        return nullable
    }

    // Precompute FIRST sets for all symbols

    private fun computeFirstSets(): Map<Symbol, Set<Terminal>> {
        val firstMap = mutableMapOf<Symbol,MutableSet<Terminal>>()

        grammar.getGrammar().flatMap { listOf(it.symbol) + it.expressions }.toSet().forEach{symbol ->
            when(symbol){
                is Terminal -> firstMap[symbol] = mutableSetOf(symbol)
                is Nonterminal -> firstMap[symbol] = mutableSetOf()
            }
        }

        // Fixed point

        var changed: Boolean
        do{
            changed = false
            for (prod in grammar.getGrammar()){
                for(symbol in prod.expressions){
                    // Add FIRST(symbol) to FIRST(head)
                    val firstToAdd = firstMap[symbol]?.filterNot {it === EOF} ?: emptySet()

                    if(firstMap[prod.symbol]?.addAll(firstToAdd) == true){
                        changed = true
                    }

                    // If symbol is not nullable, stop
                    if (symbol !in nullable) break
                }
            }
        }while(changed)
        return firstMap
    }

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
        // Get the symbols after the current LA position
        val afterDot = item.production.expressions.subList(item.lookahead_position+1, item.production.expressions.size)

        // Create the sequence: symbol after dot + original lookahead
        val sequence = afterDot + item.lookahead

        return firstOfSequence(sequence)
    }

    private fun firstOfSequence(sequence: List<Symbol>): Set<Terminal> {
        val result = mutableSetOf<Terminal>()

        for(symbol in sequence){
            when(symbol){
                is Terminal -> {
                    // The terminals adds themselves and stop processing
                    result.add(symbol)
                    break
                }
                is Nonterminal -> {
                    // Add all terminals from nonterminal's FIRST set
                    result.addAll(firstSet[symbol]?: emptySet())

                    // Stop if nonterminal is not nullable
                    if(symbol !in nullable) break
                }
            }
        }
        return result
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

    // Build canonical collection (LR(1) Automata)
    fun buildStates(){
        val startItem = Item(START_PRODUCTION, 0, EOF)
        val initialState = State(0,closure(setOf(startItem)))
        states.add(initialState)

        var nextStateId = 1
        val stateQueue = ArrayDeque<State>().apply { add(initialState) } // Kinda overkill declaration?

        while(stateQueue.isNotEmpty()){
            val state = stateQueue.removeFirst()
            val symbols = state.items.flatMap {it.nextSymbol()?.let{listOf(it)} ?: emptyList()}.toSet() // WTF

            symbols.forEach { sym ->
                val newItems = goto(state.items, sym)
                if(newItems.isNotEmpty()){
                    val existing = states.find {it.items == newItems}
                    val stateId = existing?.id?:nextStateId++

                    if(existing == null){
                        val newState = State(stateId,newItems)
                        states.add(newState)
                    }

                    when(sym){
                        is Nonterminal -> goto_table[state.id to sym] = stateId
                        is Terminal -> action_table[state.id to sym] = Action.Shift(stateId)
                    }
                }
            }
        }
    }

    // Merge states for LALR(1)
    fun mergeStates(){
        // Map from old states (LR(1)) to LALR(1) merged states
        val stateIdMap = mutableMapOf<Int,Int>()

        // Group states by their core (Production + LA position)
        val coreGroups = states.groupBy { state->
            state.items.map{item ->
                // Core item without LA
                Item(item.production, item.lookahead_position, EOF)
            }.toSet()
        }

        val mergedStates = mutableListOf<State>()

        coreGroups.values.forEach{group ->
            // Merge items from all states in group
            val mergedItems = group.flatMap { it.items }.toSet()

            // Use the smallest state ID from group for the new state
            val newId = group.minOf{it.id}
            val newState = State(newId,mergedItems)
            mergedStates.add(newState)

            // Map all old IDs in group to new ID
            group.forEach{oldState ->
                stateIdMap[oldState.id] = newId
            }
        }

        states.clear()
        states.addAll(mergedStates)

        // TODO: Update goto and action tables for new states

        val newGotoTable = mutableMapOf<Pair<Int,Nonterminal>, Int>()
        goto_table.forEach{(key,targetId) ->
            val (oldStateId, nonTerm) = key
            val newStateId = stateIdMap[oldStateId]?:oldStateId
            val newTargetId = stateIdMap[targetId]?: targetId
            newGotoTable[newStateId to nonTerm] = newTargetId
        }
        goto_table.clear()
        goto_table.putAll(newGotoTable)

        val newActionTable = mutableMapOf<Pair<Int,Terminal>,Action>()
        action_table.forEach{ (key,action) ->
            val (oldStateId, terminal) = key
            val newStateId = stateIdMap[oldStateId]?: oldStateId

            val newAction = when(action){
                is Action.Shift ->{
                    // Update shift target state
                    val newTarget = stateIdMap[action.state_id] ?: action.state_id
                    Action.Shift(newTarget)
                }else -> action // Nothing happens :D
            }
            newActionTable[newStateId to terminal] = newAction
        }
        action_table.clear()
        action_table.putAll(newActionTable)

    }

    // Build Action table and Goto table
    fun buildTables(){
        states.forEach{state ->
            state.items.forEach{ item ->
                if(item.lookahead_position == item.production.expressions.size){
                    // Reduce
                    if(item.production.symbol.name == "S'"){
                        action_table[state.id to EOF] = Action.Accept
                    }else{
                        action_table[state.id to item.lookahead] = Action.Reduce(item.production)
                    }
                }
            }
        }
    }

    private fun resolveConflict(state:Int, terminal: Terminal, production: Production){
        val shiftAction = action_table[state to terminal] as? Action.Shift
        val reduceAction = Action.Reduce(production)

        // Get precedence levels
        val termPrec = precedence_map[terminal.name]?:0
        val prodPrec = getProductionPrecedence(production)

        when {
            // Shift has higher precedence
            termPrec > prodPrec -> return // Keep shift

            // Reduce has higher precedence
            prodPrec > termPrec -> action_table[state to terminal] = reduceAction

            // Equal precedence, we need to use associativity
            else ->{
                // Reduce to use default left associativity
                action_table[state to terminal] = reduceAction
            }
        }
    }

    private fun getProductionPrecedence(production: Production):Int{
        // For expressions, we use operator precedence
        if(production.symbol.name == "Expression" && production.expressions.size == 3){
            val op = production.expressions[1] as? Terminal
            return precedence_map[op?.name]?:0
        }
        return 0
    }
}