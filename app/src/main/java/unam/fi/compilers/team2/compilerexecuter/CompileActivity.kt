package unam.fi.compilers.team2.compilerexecuter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import unam.fi.compilers.team2.R

class CompileActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compile)

        supportActionBar?.title = "Compilation and Execution"

        val output = intent.getStringExtra("Compiler Output")
        val compilerOutputView = findViewById<TextView>(R.id.compiler_output)
        compilerOutputView.text = output

    }
}