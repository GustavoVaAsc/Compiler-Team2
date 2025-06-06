package unam.fi.compilers.team2.parser

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import unam.fi.compilers.team2.R
import android.text.Spannable

class ParserOutputActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parser_output)

        supportActionBar?.title = "Parser"

        val output = intent.getCharSequenceExtra("Parser Output")
        val treeOutputView = findViewById<TextView>(R.id.tree_output)

        if (output != null) {
            val formatted = SpannableStringBuilder()
            val lines = output.lines()

            for ((index, line) in lines.withIndex()) {
                val start = formatted.length
                formatted.append(line + "\n")
                val end = formatted.length

                if (line.trim() == "Derivation steps:") {
                    formatted.setSpan(
                        ForegroundColorSpan(Color.parseColor("#8f0021")),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            treeOutputView.typeface = Typeface.MONOSPACE
            treeOutputView.text = formatted
        }
    }
}
