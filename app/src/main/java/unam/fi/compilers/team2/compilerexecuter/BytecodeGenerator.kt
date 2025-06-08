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
                is Assign -> bytecode.add(Bytecode.STORE(instr.target,instr.value))
                is BinaryOp -> {
                    val opInstr = when (instr.op) {
                        "+"  -> Bytecode.ADD(instr.result, instr.left, instr.right)
                        "-"  -> Bytecode.SUB(instr.result, instr.left, instr.right)
                        "*"  -> Bytecode.MUL(instr.result, instr.left, instr.right)
                        "/"  -> Bytecode.DIV(instr.result, instr.left, instr.right)
                        "%" -> Bytecode.MOD(instr.result, instr.left, instr.right)
                        "==" -> Bytecode.EQ(instr.result, instr.left, instr.right)
                        "!=" -> Bytecode.NEQ(instr.result, instr.left, instr.right)
                        "<"  -> Bytecode.LW(instr.result, instr.left, instr.right)
                        ">"  -> Bytecode.GT(instr.result, instr.left, instr.right)
                        "<=" -> Bytecode.LEQ(instr.result, instr.left, instr.right)
                        ">=" -> Bytecode.GEQ(instr.result, instr.left, instr.right)
                        else -> error("Unsupported operator: ${instr.op}")
                    }
                    bytecode.add(opInstr)
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