package unam.fi.compilers.team2.parser

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import unam.fi.compilers.team2.R

class ParserOutputActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parser_output)

        supportActionBar?.title = "Parser"

        val output = intent.getStringExtra("Parser Output")
        val treeOutputView = findViewById<TextView>(R.id.tree_output)

        if (output != null) {
            val formatted = SpannableStringBuilder()
            val lines = output.lines()

            treeOutputView.text = output
        }
    }
}