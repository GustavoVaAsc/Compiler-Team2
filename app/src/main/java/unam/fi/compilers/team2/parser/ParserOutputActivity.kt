package unam.fi.compilers.team2.parser

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
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

            for (line in lines) {
                val start = formatted.length
                formatted.append(line + "\n")
                val end = formatted.length

                // Negrita solo a líneas que parecen encabezados de nodos (sin ":")
                val trimmed = line.trim()
                if (!trimmed.contains(":")) {
                    formatted.setSpan(
                        StyleSpan(Typeface.BOLD),
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
