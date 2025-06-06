package unam.fi.compilers.team2.intermediate

sealed class IRInstruction

data class Assign(val target: String, val value:String) : IRInstruction()
data class BinaryOp(val result: String, val left: String, val op: String, val right: String): IRInstruction()