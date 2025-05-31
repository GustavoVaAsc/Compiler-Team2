package unam.fi.compilers.team2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TokenStreamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token_stream)

        val tokenTextView: TextView = findViewById(R.id.token_output)
        val tokens = intent.getStringExtra("Lexer Output")
        tokenTextView.text = tokens ?: "No tokens found"
    }
}
