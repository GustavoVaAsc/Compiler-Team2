package unam.fi.compilers.team2

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TokenStreamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token_stream)

        val output = intent.getStringExtra("Lexer Output")
        val tokenOutputView = findViewById<TextView>(R.id.token_output)

        if (output != null) {
            val formatted = SpannableStringBuilder()
            val lines = output.lines()

            for (line in lines) {
                val typeEnd = line.indexOf(' ')
                val bracketStart = line.indexOf('[')
                val bracketEnd = line.indexOf(']')

                if (typeEnd > 0 && bracketStart >= 0 && bracketEnd > bracketStart) {
                    val type = line.substring(0, typeEnd)
                    val tokenPart = line.substring(bracketStart, bracketEnd + 1)
                    val afterToken = line.substring(bracketEnd + 1).trimStart()

                    val startToken = formatted.length
                    formatted.append(tokenPart)
                    formatted.setSpan(
                        ForegroundColorSpan(Color.parseColor("#e4195c")),
                        startToken,
                        startToken + tokenPart.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    formatted.append(" - ")

                    val startType = formatted.length
                    formatted.append(type)
                    formatted.setSpan(
                        StyleSpan(Typeface.BOLD),
                        startType,
                        startType + type.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    if (afterToken.isNotEmpty()) {
                        formatted.append(" ")
                        formatted.append(afterToken)
                    }

                    formatted.append("\n")
                } else {
                    formatted.append(line).append("\n")
                }
            }

            tokenOutputView.text = formatted
        }
    }
}
