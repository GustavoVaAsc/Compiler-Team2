.text
.global _start

.type main, %function
_start:
.data
newline_str: .asciz "\n"
    ldr sp, =0x80000
    bl main
halt: b halt


main:
    push {fp, lr}
    mov fp, sp
    sub sp, sp, #256
    ldr r0, [fp, #-8]
    ldr r1, [fp, #-12]
    add r2, r0, r1
    str r2, [fp, #-4]
    ldr r0, [fp, #-4]
    str r0, [fp, #-16]
    mov r0, #1
    str r0, [fp, #-20]
    ldr r0, =2
    str r0, [fp, #-24]
L0:
    ldr r0, [fp, #-24]
    ldr r1, [fp, #-24]
    mul r2, r0, r1
    str r2, [fp, #-28]
    ldr r0, [fp, #-28]
    ldr r1, [fp, #-16]
    cmp r0, r1
    movle r2, #1
    movgt r2, #0
    str r2, [fp, #-32]
    ldr r0, [fp, #-32]
    ldr r1, [fp, #-40]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-36]
    ldr r0, [fp, #-36]
    cmp r0, #0
    bne L1
    ldr r0, [fp, #-16]
    ldr r1, [fp, #-24]
    bl __software_divide
    mov r2, r1
    str r2, [fp, #-44]
    ldr r0, [fp, #-44]
    ldr r1, [fp, #-40]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-48]
    ldr r0, [fp, #-48]
    ldr r1, [fp, #-40]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-52]
    ldr r0, [fp, #-52]
    cmp r0, #0
    bne L2
    mov r0, #0
    str r0, [fp, #-20]
    b L3
L2:
L3:
    ldr r0, [fp, #-24]
    ldr r1, [fp, #-60]
    add r2, r0, r1
    str r2, [fp, #-56]
    ldr r0, [fp, #-56]
    str r0, [fp, #-24]
    b L0
L1:
    ldr r0, [fp, #-20]
    ldr r1, [fp, #-68]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-64]
    ldr r0, [fp, #-64]
    ldr r1, [fp, #-40]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-72]
    ldr r0, [fp, #-72]
    cmp r0, #0
    bne L4
    ldr r0, =str0
    bl __print_string
    b L5
L4:
    ldr r0, =str1
    bl __print_string
L5:
    ldr r0, =0
    mov sp, fp
    pop {fp, pc}

.type __software_divide, %function
__software_divide:
    push {r4-r8, lr}
    mov r7, r0
    eor r6, r0, r1
    cmp r0, #0
    rsblt r0, r0, #0
    cmp r1, #0
    rsblt r1, r1, #0
    mov r2, #0
div_loop_0:
    cmp r0, r1
    blt div_fix_1
    sub r0, r0, r1
    add r2, r2, #1
    b div_loop_0
div_fix_1:
    cmp r7, #0
    rsblt r0, r0, #0
    tst r6, #0x80000000
    rsbne r2, r2, #0
    mov r1, r0
    mov r0, r2
    pop {r4-r8, pc}

.type __print_string, %function
__print_string:
    push {r0, r1, lr}
    ldr r1, =0x10100000
print_loop_2:
    ldrb r2, [r0], #1
    cmp r2, #0
    strneb r2, [r1]
    bne print_loop_2
    pop {r0, r1, pc}

.data
str0: .asciz "Es primo"
str1: .asciz "No es primo"
