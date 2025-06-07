package unam.fi.compilers.team2.compilerexecuter

sealed class Bytecode {
    // Data Movement
    data class LOAD(val dest: String, val src: String) : Bytecode()
    data class STORE(val dest: String, val src: String) : Bytecode()

    // Arithmetic Operations
    data class ADD(val dest: String, val left: String, val right:String) : Bytecode()
    data class SUB(val dest: String, val left: String, val right: String) : Bytecode()
    data class MUL(val dest: String, val left: String, val right: String) : Bytecode()
    data class DIV(val dest: String, val left: String, val right: String) : Bytecode()

    // Comparison operations
    data class EQ(val dest:String, val left:String, val right: String) : Bytecode()
    data class NEQ(val dest:String, val left: String, val right: String) : Bytecode()

    // Control flow
    data class JMP(val label: String) : Bytecode()
    data class JZ (val src: String, val label: String) : Bytecode()
    data class LABEL(val name: String) : Bytecode()

    // Input
    data class PRINT(val src: String) : Bytecode()

    // Function management
    data class CALL(val func: String) : Bytecode()
    data class RET(val value: String? = null) : Bytecode()

    // Type conversion
    data class TONUM(val dest: String, val src: String) : Bytecode()
    data class TOSTR(val dest: String, val src: String) : Bytecode()
}