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

class MainActivity : AppCompatActivity() {

    private lateinit var codeInput: EditText
    private lateinit var lineNumbers: TextView
    private lateinit var lexButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        codeInput = findViewById(R.id.code_input)
        lineNumbers = findViewById(R.id.line_numbers)
        lexButton = findViewById(R.id.lex_button)

        // Parsing test, nuke later :D

        val parserTester = ParserTester(this)
        parserTester.testParser()

        codeInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val lines = codeInput.lineCount
                val numberedLines = buildString {
                    for (i in 1..lines) append("$i\n")
                }
                lineNumbers.text = numberedLines
            }
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