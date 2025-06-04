package unam.fi.compilers.team2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import unam.fi.compilers.team2.parser.Parser
import unam.fi.compilers.team2.parser.ParserTester
import unam.fi.compilers.team2.ui.theme.CompilerTheme
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.Spannable
import android.graphics.Color



class MainActivity : AppCompatActivity() {

    private lateinit var codeInput: EditText
    private lateinit var lineNumbers: TextView
    private lateinit var lexButton: Button

    private fun loadWords(filename: String): List<String> {
        val inputStream = assets.open(filename)
        return inputStream.bufferedReader().useLines { lines ->
            lines.map { it.trim() }.filter { it.isNotEmpty() }.toList()
        }
    }

    private fun findIgnoredRanges(text: String): List<IntRange> {
        val ignoredRanges = mutableListOf<IntRange>()

        var i = 0
        val length = text.length
        while (i < length) {
            when {

                i + 1 < length && text[i] == '/' && text[i + 1] == '/' -> {
                    val end = text.indexOf('\n', i).let { if (it == -1) length else it }
                    ignoredRanges.add(i until end)
                    i = end
                }

                text[i] == '"' -> {
                    val start = i
                    i++
                    while (i < length) {
                        if (text[i] == '"' && text[i - 1] != '\\') {
                            i++
                            break
                        }
                        i++
                    }
                    ignoredRanges.add(start until i)
                }

                else -> i++
            }
        }
        return ignoredRanges
    }



    private fun applyHighlight(
        spannable: SpannableStringBuilder,
        words: List<String>,
        color: Int,
        text: String,
        ignoredRanges: List<IntRange>
    ) {
        fun isInIgnoredRange(start: Int, end: Int): Boolean {
            return ignoredRanges.any { range -> start < range.endInclusive && end > range.start }
        }

        for (word in words) {
            var index = text.indexOf(word)
            while (index >= 0) {
                val start = index
                val end = index + word.length

                if (!isInIgnoredRange(start, end)) {
                    val beforeChar = if (start > 0) text[start - 1] else ' '
                    val afterChar = if (end < text.length) text[end] else ' '
                    if (!beforeChar.isLetterOrDigit() && !afterChar.isLetterOrDigit()) {
                        spannable.setSpan(
                            ForegroundColorSpan(color),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                index = text.indexOf(word, index + word.length)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        codeInput = findViewById(R.id.code_input)
        lineNumbers = findViewById(R.id.line_numbers)
        lexButton = findViewById(R.id.lex_button)

        // Load words from .txt

        val datatypes = loadWords("Datatypes.txt")
        val keywords = loadWords("Keywords.txt")

        // Define colors for words

        val dtColor = Color.parseColor("#E4195C")
        val kwColor = Color.parseColor("#8F0021")

        // Parsing test, nuke later :D

        val parserTester = ParserTester(this)
        parserTester.testParser()

        codeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                val spannable = SpannableStringBuilder(text)
                val ignoredRanges = findIgnoredRanges(text)

                spannable.getSpans(0, spannable.length, ForegroundColorSpan::class.java).forEach {
                    spannable.removeSpan(it)
                }

                applyHighlight(spannable, datatypes, dtColor, text, ignoredRanges)
                applyHighlight(spannable, keywords, kwColor, text, ignoredRanges)


                codeInput.removeTextChangedListener(this)
                codeInput.text = spannable
                codeInput.setSelection(spannable.length)
                codeInput.addTextChangedListener(this)

                val lines = codeInput.lineCount
                lineNumbers.text = buildString {
                    for (i in 1..lines) append("$i\n")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        lexButton.setOnClickListener{
            val code: String = codeInput.text.toString()
            val lexemes: ArrayList<StringBuilder> = code.lines().map{StringBuilder(it)} as ArrayList<StringBuilder>

            val lexer = unam.fi.compilers.team2.lexer.Lexer(lexemes,this)
            val tokens = lexer.tokenize()

            val tokenOutput = tokens.joinToString("\n") {
                it.toString()
            }

            val intent: Intent = Intent(this, TokenStreamActivity::class.java)
            intent.putExtra("Lexer Output",tokenOutput)
            startActivity(intent)
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CompilerTheme {
        Greeting("Android")
    }
}