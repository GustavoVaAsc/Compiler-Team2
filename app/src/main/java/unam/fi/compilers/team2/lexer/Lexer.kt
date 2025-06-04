package unam.fi.compilers.team2.lexer

import android.content.Context
import android.content.res.AssetManager
import androidx.compose.runtime.key
import java.io.IOException

class Lexer (lexemes:ArrayList<StringBuilder>, context:Context){
    private lateinit var lexemes:MutableList<StringBuilder> // Lexemes List
    private lateinit var token_stream:MutableList<Token> // Token stream
    private lateinit var keywords:MutableSet<String>
    private lateinit var datatypes:MutableSet<String>
    private lateinit var categories:MutableList<String>
    private lateinit var token_classification:MutableMap<String,MutableSet<String>>
    private var total_tokens:Int = 0

    init {
        this.lexemes = lexemes
        this.keywords = mutableSetOf()
        this.datatypes = mutableSetOf()
        this.token_stream = mutableListOf()
        this.categories = mutableListOf()
        this.token_classification = mutableMapOf()

        // Initialize categories
        this.categories.add("Keyword")
        this.categories.add("Identifier")
        this.categories.add("Operator")
        this.categories.add("Boolean")
        this.categories.add("Relation")
        this.categories.add("Punctuation")
        this.categories.add("Constant")
        this.categories.add("Literal")
        this.categories.add("Datatype")
        this.categories.add("Unknown")

        try {
            // Read keywords from file
            val keywordReader = context.assets.open("Keywords.txt").bufferedReader().useLines { lines->
                lines.forEach { line ->
                    keywords.add(line)
                }
            }

            val datatypeReader = context.assets.open("Datatypes.txt").bufferedReader().useLines { lines->
                lines.forEach { line ->
                    datatypes.add(line)
                }
            }

        }catch(e: IOException){
            e.printStackTrace()
        }
    }

    // Method to create keyword regex
    private fun buildRegex(set: Set<String>): String {
        return set.sortedByDescending { it.length }.joinToString("|") { Regex.escape(it) }
    }


    // Tokenize method
    public fun tokenize():List<Token>{
        val keywordRegex:Regex= Regex(this.buildRegex(keywords))
        val datatypeRegex:Regex= Regex(this.buildRegex(datatypes))

        // Define other regex
        val idRegex:Regex = Regex("[a-zA-Z_][a-zA-Z0-9_]*")
        val opRegex = Regex(">>=|<<=|\\+=|-=|\\*=|/=|%=|&&|\\|\\||\\+\\+|--|&=|\\|=|\\^=|=|!|\\+|-|\\*|/|%|&|\\||\\^")
        val relRegex:Regex = Regex("==|!=|>=|<=|>|<")
        val puntRegex = Regex("\\*|\\(|\\)|\\.|,|:|;|\\{|\\}|->")
        val boolRegex = Regex("\\b(true|false)\\b")
        val constRegex:Regex = Regex("-?[0-9]+(\\.[0-9]+)?")
        val litRegex:Regex = Regex("\"([^\"\\\\]|\\\\.)*\"")

        var n:Int = this.lexemes.size
        for(i in lexemes.indices){
            // Delete comments
            val lexeme:String = this.lexemes[i].toString()
            val clearedLexeme:String = lexeme.replace("//.*","").trim()
            if(clearedLexeme.isEmpty()){
                lexemes[i] = StringBuilder("\n")
                continue
            }else{
                lexemes[i] = StringBuilder(clearedLexeme)
            }

            val noLitLexeme: String = litRegex.replace(clearedLexeme) { matchResult ->
                " ".repeat(matchResult.value.length)
            }.trim()

            val noDatatypeLexeme: String = datatypeRegex.replace(noLitLexeme) { matchResult ->
                " ".repeat(matchResult.value.length)
            }

            val noIdentifiersLexeme: String = idRegex.replace(noDatatypeLexeme) { matchResult ->
                " ".repeat(matchResult.value.length)
            }



            // TODO: Apply DRY to this:
            classifyAndCount(litRegex, clearedLexeme, "Literal", i)
            classifyAndCount(constRegex, noIdentifiersLexeme, "Constant", i)
            classifyAndCount(keywordRegex, noLitLexeme, "Keyword", i)
            classifyAndCount(datatypeRegex, noLitLexeme, "Datatype", i)
            classifyAndCount(boolRegex, noLitLexeme, "Boolean", i)
            classifyAndCount(relRegex, noLitLexeme, "Relation", i)
            classifyAndCount(opRegex, noLitLexeme, "Operator", i)
            classifyAndCount(puntRegex, noLitLexeme, "Punctuation", i)
            classifyAndCount(idRegex, noDatatypeLexeme, "Identifier", i)

        }
        return this.token_stream
    }

    fun IntRange.overlaps(other: IntRange): Boolean {
        return this.first <= other.last && other.first <= this.last
    }

    private fun classifyAndCount(regex:Regex, text:String, category:String, line:Int){
        for(match in regex.findAll(text)){
            val matchedRanges = mutableSetOf<IntRange>()
            val token = match.value
            val col = match.range.first + 1

            val range = match.range
            if (matchedRanges.any { it.overlaps(range) }) continue
            matchedRanges.add(range)

            if(category == "Identifier"){
                if(token !in keywords){
                    token_classification.getOrPut(category) { mutableSetOf() }.add(token)
                    this.token_stream.add(Token(category, token, line, col))
                }
            }else{
                token_classification.getOrPut(category) { mutableSetOf() }.add(token)
                this.token_stream.add(Token(category, token, line, col))
            }

            if(token !in keywords || category == "Identifier"){
                this.total_tokens++
            }
        }
    }

    public fun getTotalTokens():Int = total_tokens
}