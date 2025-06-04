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
import unam.fi.compilers.team2.ui.theme.CompilerTheme
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.Spannable
import android.graphics.Color
import android.widget.ScrollView




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

    private fun applyCommentHighlight(spannable: SpannableStringBuilder, text: String, color: Int) {
        val regex = Regex("//.*$")
        val matches = regex.findAll(text)
        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            spannable.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
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

        val validEnd = listOf(' ', '\n', '\t', '(', '{', ';', ')', '}', ',', ':')
        val validStart = listOf(' ', '\n', '\t', '(', '{', ';', ')', '}', ',', ':')

        for (word in words) {
            var index = text.indexOf(word)
            val isSymbol = word.length == 1 && !word[0].isLetterOrDigit()

            while (index >= 0) {
                val start = index
                val end = index + word.length

                if (!isInIgnoredRange(start, end)) {
                    val beforeChar = if (start > 0) text[start - 1] else ' '
                    val afterChar = if (end < text.length) text[end] else ' '

                    if (isSymbol || (
                                (start == 0 || beforeChar in validStart) &&
                                        (end == text.length || afterChar in validEnd)
                                )) {

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

        val commentColor = Color.parseColor("#A9D6BB")

        // Define colors for words
        val fileColorMap = mapOf(
            "Datatypes.txt" to Color.parseColor("#E4195C"),
            "Keywords.txt" to Color.parseColor("#2172ff"),
            "GroupS.txt" to Color.parseColor("#F2C572"),
        )
        // Load words from .txt
        val wordColorMap = fileColorMap.map { (file, color) ->
            loadWords(file) to color
        }

        codeInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                codeInput.removeTextChangedListener(this)

                val text = s.toString()

                val spannable = SpannableStringBuilder(text)

                val ignoredRanges = findIgnoredRanges(text)

                for ((words, color) in wordColorMap) {
                    applyHighlight(spannable, words, color, text, ignoredRanges)
                }

                applyCommentHighlight(spannable, text, commentColor)

                val selectionStart = codeInput.selectionStart
                val selectionEnd = codeInput.selectionEnd
                codeInput.text = spannable
                codeInput.setSelection(
                    selectionStart.coerceAtMost(codeInput.text.length),
                    selectionEnd.coerceAtMost(codeInput.text.length)
                )


                codeInput.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
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