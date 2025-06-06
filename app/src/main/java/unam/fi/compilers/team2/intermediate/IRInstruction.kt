package unam.fi.compilers.team2.intermediate

sealed class IRInstruction

data class Assign(val target: String, val value: String) : IRInstruction() {
    override fun toString(): String = "$target = $value"
}

data class BinaryOp(val result: String, val left: String, val op: String, val right: String) : IRInstruction() {
    override fun toString(): String = "$result = $left $op $right"
}

data class Label(val name: String) : IRInstruction() {
    override fun toString(): String = "$name:"
}

data class Goto(val label: String) : IRInstruction() {
    override fun toString(): String = "goto $label"
}

data class IfGoto(val condition: String, val label: String) : IRInstruction() {
    override fun toString(): String = "if $condition goto $label"
}

data class Return(val value: String?) : IRInstruction() {
    override fun toString(): String = if (value != null) "return $value" else "return"
}

data class Call(val function: String) : IRInstruction() {
    override fun toString(): String = "call $function"
}

data class IRPrint(val value: String) : IRInstruction() {
    override fun toString(): String = "print $value"
}

data class UnaryOp(val result: String, val op: String, val operand: String) : IRInstruction() {
    override fun toString(): String = "$result = $op$operand"
}
