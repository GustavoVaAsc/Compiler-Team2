package unam.fi.compilers.team2.compilerexecuter

sealed class Bytecode {
    // Data Movement
    data class LOAD(val dest: String, val src: String) : Bytecode() {
        override fun toString() = "LOAD $dest <- $src"
    }

    data class STORE(val dest: String, val src: String) : Bytecode() {
        override fun toString() = "STORE $dest <- $src"
    }

    // Arithmetic Operations
    data class ADD(val dest: String, val left: String, val right:String) : Bytecode() {
        override fun toString() = "ADD $dest <- $left + $right"
    }

    data class SUB(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "SUB $dest <- $left - $right"
    }

    data class MUL(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "MUL $dest <- $left * $right"
    }

    data class DIV(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "DIV $dest <- $left / $right"
    }

    data class MOD(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "MOD $dest <- $left % $right"
    }

    // Comparison operations
    data class EQ(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "EQ $dest <- $left == $right"
    }

    data class NEQ(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "NEQ $dest <- $left != $right"
    }

    data class LW(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "LW $dest <- $left < $right"
    }

    data class GT(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "GT $dest <- $left > $right"
    }

    data class LEQ(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "LEQ $dest <- $left <= $right"
    }

    data class GEQ(val dest: String, val left: String, val right: String) : Bytecode() {
        override fun toString() = "GEQ $dest <- $left >= $right"
    }

    // Control flow
    data class JMP(val label: String) : Bytecode() {
        override fun toString() = "JMP $label"
    }

    data class JZ (val src: String, val label: String) : Bytecode() {
        override fun toString() = "JZ if $src == 0 -> $label"
    }

    data class LABEL(val name: String) : Bytecode() {
        override fun toString() = "LABEL $name:"
    }

    // Input
    data class PRINT(val src: String) : Bytecode() {
        override fun toString() = "PRINT $src"
    }

    // Function management
    data class CALL(val func: String) : Bytecode() {
        override fun toString() = "CALL $func"
    }

    data class RET(val value: String? = null) : Bytecode() {
        override fun toString() = value?.let { "RET $it" } ?: "RET"
    }

    // Type conversion
    data class TONUM(val dest: String, val src: String) : Bytecode() {
        override fun toString() = "TONUM $dest <- $src"
    }

    data class TOSTR(val dest: String, val src: String) : Bytecode() {
        override fun toString() = "TOSTR $dest <- $src"
    }
}
