package unam.fi.compilers.team2.lexer

import android.content.Context
import android.content.res.AssetManager
import androidx.compose.runtime.key
import java.io.IOException

class Lexer (lexemes:ArrayList<StringBuilder>, context:Context){
    private lateinit var lexemes:MutableList<StringBuilder> // Lexemes List
    private lateinit var token_stream:MutableList<Token> // Token stream
    private lateinit var keywords:MutableSet<String> // Stores Keywords from Keywords.txt
    private lateinit var datatypes:MutableSet<String> // Stores Datatypes ...
    private lateinit var categories:MutableList<String>
    private lateinit var token_classification:MutableMap<String,MutableSet<String>>
    private var total_tokens:Int = 0

    // Class constructor
    init {
        // Initialize Data Structures
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
        this.categories.add("INTEGER")
        this.categories.add("FLOAT")
        this.categories.add("STRING")

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

        // Define the regex for recognizing the other tokens
        val idRegex:Regex = Regex("[a-zA-Z_][a-zA-Z0-9_]*")
        val opRegex = Regex(">>=|<<=|\\+=|-=|\\*=|/=|%=|&&|\\|\\||\\+\\+|--|&=|\\|=|\\^=|=|!|\\+|-|\\*|/|%|&|\\||\\^")
        val relRegex:Regex = Regex("==|!=|>=|<=|>|<")
        val puntRegex = Regex("\\*|\\(|\\)|\\.|,|:|;|\\{|\\}|->")
        val boolRegex = Regex("\\b(true|false)\\b")
        val litRegex:Regex = Regex("\"([^\"\\\\]|\\\\.)*\"")
        val floatRegex = Regex("-?\\d+\\.\\d+")
        val intRegex = Regex("-?\\d+")

        var n:Int = this.lexemes.size

        // Iterate all lexemes
        for(i in lexemes.indices){
            // Delete comments
            val lexeme:String = this.lexemes[i].toString()
            val clearedLexeme:String = Regex("//.*").replace(lexeme, "").trim()
            if(clearedLexeme.isEmpty()){
                lexemes[i] = StringBuilder("\n")
                continue
            }else{
                lexemes[i] = StringBuilder(clearedLexeme)
            }

            val lineTokens = mutableListOf<Token>()

            // Inner function, collects the tokens and guarantees they don't overlap
            fun classifyAndCollect(regex: Regex, category: String) {
                for (match in regex.findAll(clearedLexeme)) {
                    val tokenValue = match.value
                    val col = match.range.first + 1
                    val range = match.range

                    // Skip if overlapping with existing token with the line and column
                    if (lineTokens.any {
                            it.getTokenColumn() - 1 <= range.last && // We use range to check the entire token
                                    range.first <= (it.getTokenColumn() - 1 + it.getTokenValue().length - 1)
                        }) continue

                    val token = when (category) {
                        // Check if the current token of the ID category is not a reserved word.
                        "Identifier" -> {
                            if (tokenValue !in keywords) Token(category, tokenValue, i + 1, col)
                            else null
                        }
                        else -> Token(category, tokenValue, i + 1, col)
                    }

                    // Add them to token_classification
                    token?.let {
                        lineTokens.add(it)
                        token_classification.getOrPut(category) { mutableSetOf() }.add(tokenValue)
                    }
                }
            }

            // We process each category, starting by the constant values
            classifyAndCollect(litRegex, "STRING")
            classifyAndCollect(floatRegex, "FLOAT")
            classifyAndCollect(intRegex, "INTEGER")
            classifyAndCollect(boolRegex, "Boolean")
            classifyAndCollect(keywordRegex, "Keyword")
            classifyAndCollect(datatypeRegex, "Datatype")
            classifyAndCollect(relRegex, "Relation")
            classifyAndCollect(opRegex, "Operator")
            classifyAndCollect(puntRegex, "Punctuation")
            classifyAndCollect(idRegex, "Identifier")

            // Sort tokens by position in line
            lineTokens.sortBy { it.getTokenColumn() }
            token_stream.addAll(lineTokens)
        }
        this.total_tokens = token_stream.size // Number of tokens processed
        return this.token_stream
    }

    // Function to check if two tokens are overlapping
    fun IntRange.overlaps(other: IntRange): Boolean {
        return this.first <= other.last && other.first <= this.last
    }


}