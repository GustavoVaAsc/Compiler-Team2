package unam.fi.compilers.team2.codegen

import unam.fi.compilers.team2.intermediate.*
import java.io.File

class TargetCodeGenerator {
    private val assembly = StringBuilder()
    private val dataSection = StringBuilder()
    private var stringCounter = 0
    private var labelCounter = 0
    private val stackOffsets = mutableMapOf<String, Int>()
    private var stackSizeForCurrentFunction = 0

    private val CPUlator_TTY_ADDRESS = "0x10100000"

    fun generate(instructions: List<IRInstruction>): String {
        assembly.append(".text\n.global _start\n\n")
        assembly.append(".type main, %function\n")
        assembly.append("_start:\n")
        assembly.append(".data\n")
        assembly.append("newline_str: .asciz \"\\n\"\n")
        assembly.append("    ldr sp, =0x80000\n")
        assembly.append("    bl main\n")
        assembly.append("halt: b halt\n\n")

        instructions.forEach { instr ->
            when (instr) {
                is Label -> handleLabel(instr)
                is Assign -> handleAssign(instr)
                is BinaryOp -> handleBinaryOp(instr)
                is UnaryOp -> handleUnaryOp(instr)
                is IfGoto -> handleIfGoto(instr)
                is Goto -> handleGoto(instr)
                is IRPrint -> handlePrint(instr)
                is Return -> handleReturn(instr)
                is Call -> handleCall(instr)
            }
        }

        appendSoftwareDivide()
        appendPrintString()

        if (dataSection.isNotEmpty()) {
            assembly.append("\n.data\n")
            assembly.append(dataSection.toString())
        }

        return assembly.toString()
    }

    private fun handleLabel(instr: Label) {
        when {
            instr.name.startsWith("func_") && instr.name.endsWith("_start") -> {
                val functionName = instr.name.removePrefix("func_").removeSuffix("_start")
                stackOffsets.clear()
                stackSizeForCurrentFunction = 0
                assembly.append("\n$functionName:\n")
                assembly.append("    push {fp, lr}\n")
                assembly.append("    mov fp, sp\n")
                assembly.append("    sub sp, sp, #256\n")
            }
            instr.name.endsWith("_end") -> {}
            else -> assembly.append("${instr.name}:\n")
        }
    }

    private fun getStackOffset(name: String): Int {
        return stackOffsets.getOrPut(name) {
            stackSizeForCurrentFunction += 4
            stackSizeForCurrentFunction
        }
    }

    private fun handleAssign(instr: Assign) {
        val destOffset = getStackOffset(instr.target)
        when {
            instr.value == "true" -> assembly.append("    mov r0, #1\n")
            instr.value == "false" -> assembly.append("    mov r0, #0\n")
            instr.value.matches(Regex("-?\\d+")) -> assembly.append("    ldr r0, =${instr.value}\n")
            instr.value.startsWith("\"") -> {
                val strLabel = "str${stringCounter++}"
                dataSection.append("$strLabel: .asciz ${instr.value}\n")
                assembly.append("    ldr r0, =$strLabel\n")
            }
            else -> {
                val srcOffset = getStackOffset(instr.value)
                assembly.append("    ldr r0, [fp, #-$srcOffset]\n")
            }
        }
        assembly.append("    str r0, [fp, #-$destOffset]\n")
    }

    private fun handleBinaryOp(instr: BinaryOp) {
        val destOffset = getStackOffset(instr.result)
        val leftOffset = getStackOffset(instr.left)
        val rightOffset = getStackOffset(instr.right)

        assembly.append("    ldr r0, [fp, #-$leftOffset]\n")
        assembly.append("    ldr r1, [fp, #-$rightOffset]\n")

        when (instr.op) {
            "+" -> assembly.append("    add r2, r0, r1\n")
            "-" -> assembly.append("    sub r2, r0, r1\n")
            "*" -> assembly.append("    mul r2, r0, r1\n")
            "/" -> {
                assembly.append("    bl __software_divide\n")
                assembly.append("    mov r2, r0\n")
            }
            "%" -> {
                assembly.append("    bl __software_divide\n")
                assembly.append("    mov r2, r1\n")
            }
            "==" -> {
                assembly.append("    cmp r0, r1\n")
                assembly.append("    moveq r2, #1\n")
                assembly.append("    movne r2, #0\n")
            }
            "!=" -> {
                assembly.append("    cmp r0, r1\n")
                assembly.append("    movne r2, #1\n")
                assembly.append("    moveq r2, #0\n")
            }
            "<" -> {
                assembly.append("    cmp r0, r1\n")
                assembly.append("    movlt r2, #1\n")
                assembly.append("    movge r2, #0\n")
            }
            "<=" -> {
                assembly.append("    cmp r0, r1\n")
                assembly.append("    movle r2, #1\n")
                assembly.append("    movgt r2, #0\n")
            }
            ">" -> {
                assembly.append("    cmp r0, r1\n")
                assembly.append("    movgt r2, #1\n")
                assembly.append("    movle r2, #0\n")
            }
            ">=" -> {
                assembly.append("    cmp r0, r1\n")
                assembly.append("    movge r2, #1\n")
                assembly.append("    movlt r2, #0\n")
            }
        }

        assembly.append("    str r2, [fp, #-$destOffset]\n")
    }

    private fun handleUnaryOp(instr: UnaryOp) {
        val destOffset = getStackOffset(instr.result)
        val operandOffset = getStackOffset(instr.operand)
        assembly.append("    ldr r0, [fp, #-$operandOffset]\n")
        when (instr.op) {
            "-" -> assembly.append("    rsb r1, r0, #0\n")
            "!" -> {
                assembly.append("    cmp r0, #0\n")
                assembly.append("    moveq r1, #1\n")
                assembly.append("    movne r1, #0\n")
            }
        }
        assembly.append("    str r1, [fp, #-$destOffset]\n")
    }

    private fun handleIfGoto(instr: IfGoto) {
        val condOffset = getStackOffset(instr.condition)
        assembly.append("    ldr r0, [fp, #-$condOffset]\n")
        assembly.append("    cmp r0, #0\n")
        assembly.append("    bne ${instr.label}\n")
    }

    private fun handleGoto(instr: Goto) {
        assembly.append("    b ${instr.label}\n")
    }

    private fun handlePrint(instr: IRPrint) {
        when {
            instr.value.startsWith("\"") -> {
                val strLabel = "str${stringCounter++}"
                dataSection.append("$strLabel: .asciz ${instr.value}\n")
                assembly.append("    ldr r0, =$strLabel\n")
                assembly.append("    bl __print_string\n")
            }
            instr.value == "true" -> {
                val trueLabel = "true${stringCounter++}"
                dataSection.append("$trueLabel: .asciz \"true\\n\"\n")
                assembly.append("    ldr r0, =$trueLabel\n")
                assembly.append("    bl __print_string\n")
            }
            instr.value == "false" -> {
                val falseLabel = "false${stringCounter++}"
                dataSection.append("$falseLabel: .asciz \"false\\n\"\n")
                assembly.append("    ldr r0, =$falseLabel\n")
                assembly.append("    bl __print_string\n")
            }
        }
    }

    private fun handleReturn(instr: Return) {
        if (instr.value != null) {
            if (instr.value.matches(Regex("-?\\d+"))) {
                assembly.append("    ldr r0, =${instr.value}\n")
            } else {
                val offset = getStackOffset(instr.value)
                assembly.append("    ldr r0, [fp, #-$offset]\n")
            }
        }
        assembly.append("    mov sp, fp\n")
        assembly.append("    pop {fp, pc}\n")
    }

    private fun handleCall(instr: Call) {
        assembly.append("    bl ${instr.function}\n")
    }

    private fun appendSoftwareDivide() {
        val loop = "div_loop_${labelCounter++}"
        val fix = "div_fix_${labelCounter++}"

        assembly.append("\n.type __software_divide, %function\n__software_divide:\n")
        assembly.append("    push {r4-r8, lr}\n")
        assembly.append("    mov r7, r0\n")
        assembly.append("    eor r6, r0, r1\n")
        assembly.append("    cmp r0, #0\n    rsblt r0, r0, #0\n")
        assembly.append("    cmp r1, #0\n    rsblt r1, r1, #0\n")
        assembly.append("    mov r2, #0\n$loop:\n")
        assembly.append("    cmp r0, r1\n    blt $fix\n")
        assembly.append("    sub r0, r0, r1\n    add r2, r2, #1\n    b $loop\n")
        assembly.append("$fix:\n    cmp r7, #0\n    rsblt r0, r0, #0\n")
        assembly.append("    tst r6, #0x80000000\n    rsbne r2, r2, #0\n")
        assembly.append("    mov r1, r0\n    mov r0, r2\n    pop {r4-r8, pc}\n")
    }

    private fun appendPrintString() {
        val loop = "print_loop_${labelCounter++}"
        assembly.append("\n.type __print_string, %function\n__print_string:\n")
        assembly.append("    push {r0, r1, lr}\n")
        assembly.append("    ldr r1, =${CPUlator_TTY_ADDRESS}\n")
        assembly.append("$loop:\n")
        assembly.append("    ldrb r2, [r0], #1\n")
        assembly.append("    cmp r2, #0\n")
        assembly.append("    strneb r2, [r1]\n")
        assembly.append("    bne $loop\n")
        assembly.append("    pop {r0, r1, pc}\n")
    }

}
