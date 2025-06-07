package unam.fi.compilers.team2.compilerexecuter

class StackVM {
    private val registers = mutableMapOf<String,Any>()
    private val call_stack = ArrayDeque<MutableMap<String,Any>>()
    private var pc = 0
    private var current_bytecode = listOf<Bytecode>()
    private val labels = mutableMapOf<String,Int>()
    private val program_output = StringBuilder("")

    public fun execute(bytecode: List<Bytecode>):String{
        current_bytecode = bytecode
        buildLabelIndex()
        call_stack.addLast(mutableMapOf())
        pc = 0

        while (pc < bytecode.size){
            when (val instr = bytecode[pc++]){
                is Bytecode.LOAD -> load(instr)
                is Bytecode.STORE -> store(instr)
                is Bytecode.ADD -> arithmetic(instr, ::handleAdd)
                is Bytecode.SUB -> arithmetic(instr, ::handleSub)
                is Bytecode.MUL -> arithmetic(instr, ::handleMul)
                is Bytecode.DIV -> arithmetic(instr, ::handleDiv)
                is Bytecode.EQ -> compare(instr, ::handleEq)
                is Bytecode.NEQ -> compare(instr, ::handleNeq)
                is Bytecode.JMP -> jump(instr.label)
                is Bytecode.JZ -> if(getValue(instr.src) == 0) jump(instr.label)
                is Bytecode.LABEL -> {}
                is Bytecode.PRINT -> program_output.append(getValue(instr.src)).append("\n")
                is Bytecode.CALL -> callFunction(instr.func)
                is Bytecode.RET -> returnFunction(instr.value)
                is Bytecode.TONUM -> convertToNumber(instr)
                is Bytecode.TOSTR -> convertToString(instr)
            }
        }
        return program_output.toString()
    }

    private fun load(instr:Bytecode.LOAD){
        registers[instr.dest] = getValue(instr.src)
    }

    private fun store(instr: Bytecode.STORE){
        call_stack.first()[instr.dest] = getValue(instr.src)
    }

    private fun arithmetic(
        instr: Bytecode,
        opHandler: (Any, Any) -> Any
    ) {
        val (dest, left, right) = when (instr) {
            is Bytecode.ADD -> Triple(instr.dest, instr.left, instr.right)
            is Bytecode.SUB -> Triple(instr.dest, instr.left, instr.right)
            is Bytecode.MUL -> Triple(instr.dest, instr.left, instr.right)
            is Bytecode.DIV -> Triple(instr.dest, instr.left, instr.right)
            else -> error("Invalid arithmetic instruction")
        }
        val leftVal = getValue(left)
        val rightVal = getValue(right)
        registers[dest] = opHandler(leftVal, rightVal)
    }


    private fun compare(
        instr: Bytecode,
        cmpHandler: (Any, Any) -> Boolean
    ) {
        val (dest, left, right) = when (instr) {
            is Bytecode.EQ -> Triple(instr.dest, instr.left, instr.right)
            is Bytecode.NEQ -> Triple(instr.dest, instr.left, instr.right)
            else -> error("Invalid comparison instruction")
        }
        val leftVal = getValue(left)
        val rightVal = getValue(right)
        registers[dest] = if (cmpHandler(leftVal, rightVal)) 1 else 0
    }

    private fun convertToNumber(instr: Bytecode.TONUM) {
        val value = getValue(instr.src)
        registers[instr.dest] = when (value) {
            is Number -> value
            is String -> value.toDoubleOrNull() ?: 0.0
            else -> 0
        }
    }

    private fun convertToString(instr: Bytecode.TOSTR) {
        registers[instr.dest] = getValue(instr.src).toString()
    }

    private fun handleAdd(left: Any, right: Any): Any {
        return when {
            left is String || right is String -> left.toString() + right.toString()
            left is Int && right is Int -> left + right
            left is Number && right is Number -> left.toDouble() + right.toDouble()
            else -> error("Unsupported types for addition")
        }
    }

    private fun handleSub(left: Any, right: Any): Any {
        return when {
            left is Int && right is Int -> left - right
            left is Number && right is Number -> left.toDouble() - right.toDouble()
            else -> error("Unsupported types for subtraction")
        }
    }

    private fun handleMul(left: Any, right: Any): Any {
        return when {
            left is Int && right is Int -> left * right
            left is Number && right is Number -> left.toDouble() * right.toDouble()
            else -> error("Unsupported types for multiplication")
        }
    }

    private fun handleDiv(left: Any, right: Any): Any {
        return when {
            left is Int && right is Int -> if (right != 0) left / right else 0
            left is Number && right is Number ->
                if (right.toDouble() != 0.0) left.toDouble() / right.toDouble() else 0.0
            else -> error("Unsupported types for division")
        }
    }

    private fun handleEq(left: Any, right: Any): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() == right.toDouble()
            else -> left == right
        }
    }

    private fun handleNeq(left: Any, right: Any): Boolean {
        return !handleEq(left, right)
    }

    private fun jump(label: String){
        pc = labels[label]?: error("Undefined label: $label")
    }

    private fun callFunction(func:String){
        call_stack.addLast(registers.toMutableMap())
        registers.clear()
        jump("func_${func}_start")
    }

    private fun returnFunction(value: String?){
        val result = value?.let {getValue(it)}
        call_stack.removeLast().let{registers.putAll(it)}
        value?.let {registers["ret"] = result!!}
        jump("call_return")
    }

    private fun getValue(src: String): Any {
        return when {
            src.startsWith("t") -> registers[src] ?: error("Undefined register $src")
            src.startsWith("\"") -> src.substring(1, src.length - 1)  // Remove quotes
            src.toIntOrNull() != null -> src.toInt()
            src.toDoubleOrNull() != null -> src.toDouble()
            else -> {
                // Search call stack from top to bottom
                for (i in call_stack.size - 1 downTo 0) {
                    call_stack[i][src]?.let { return it }
                }
                error("Undefined variable: $src")
            }
        }
    }

    private fun getValueAsBoolean(src: String): Boolean {
        val value = getValue(src)
        return when (value) {
            is Number -> value.toDouble() != 0.0
            is String -> value.isNotEmpty()
            else -> false
        }
    }

    private fun printValue(value: Any) {
        println(
            when (value) {
                is Double -> "%.2f".format(value)
                else -> value.toString()
            }
        )
    }

    private fun buildLabelIndex(){
        current_bytecode.forEachIndexed{index, instr ->
            if(instr is Bytecode.LABEL) labels[instr.name] = index
        }
    }

}