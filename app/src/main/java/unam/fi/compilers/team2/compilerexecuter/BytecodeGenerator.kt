package unam.fi.compilers.team2.compilerexecuter

import unam.fi.compilers.team2.intermediate.Assign
import unam.fi.compilers.team2.intermediate.BinaryOp
import unam.fi.compilers.team2.intermediate.Call
import unam.fi.compilers.team2.intermediate.Goto
import unam.fi.compilers.team2.intermediate.IRInstruction
import unam.fi.compilers.team2.intermediate.IRPrint
import unam.fi.compilers.team2.intermediate.IfGoto
import unam.fi.compilers.team2.intermediate.Label
import unam.fi.compilers.team2.intermediate.Return

class BytecodeGenerator {
    fun generate(ir: List<IRInstruction>): List<Bytecode> {
        val bytecode = mutableListOf<Bytecode>()

        ir.forEach { instr ->
            when (instr) {
                is Label -> bytecode.add(Bytecode.LABEL(instr.name))
                is Assign -> bytecode.add(Bytecode.STORE(instr.value, instr.target))
                is BinaryOp -> {
                    val opcode = when (instr.op) {
                        "+" -> Bytecode.ADD::class
                        "-" -> Bytecode.SUB::class
                        "*" -> Bytecode.MUL::class
                        "/" -> Bytecode.DIV::class
                        "==" -> Bytecode.EQ::class
                        "!=" -> Bytecode.NEQ::class
                        else -> error("Unsupported operator: ${instr.op}")
                    }
                    bytecode.add(
                        opcode.constructors.first().call(
                            instr.result, instr.left, instr.right
                        ) as Bytecode
                    )
                }
                is IfGoto -> bytecode.add(Bytecode.JZ(instr.condition, instr.label))
                is Goto -> bytecode.add(Bytecode.JMP(instr.label))
                is IRPrint -> bytecode.add(Bytecode.PRINT(instr.value))
                is Return -> bytecode.add(Bytecode.RET(instr.value.takeIf { it != "void" }))
                is Call -> bytecode.add(Bytecode.CALL(instr.function))
                else -> {}
            }
        }
        return bytecode
    }
}