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
    ldr r0, =0
    str r0, [fp, #-4]
    ldr r0, =10
    str r0, [fp, #-8]
    ldr r0, =1
    str r0, [fp, #-12]
L0:
    ldr r0, [fp, #-12]
    ldr r1, [fp, #-8]
    cmp r0, r1
    movle r2, #1
    movgt r2, #0
    str r2, [fp, #-16]
    ldr r0, [fp, #-16]
    ldr r1, [fp, #-24]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-20]
    ldr r0, [fp, #-20]
    cmp r0, #0
    bne L1
    ldr r0, [fp, #-12]
    ldr r1, [fp, #-32]
    bl __software_divide
    mov r2, r1
    str r2, [fp, #-28]
    ldr r0, [fp, #-28]
    ldr r1, [fp, #-24]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-36]
    ldr r0, [fp, #-36]
    ldr r1, [fp, #-24]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-40]
    ldr r0, [fp, #-40]
    cmp r0, #0
    bne L2
    ldr r0, [fp, #-4]
    ldr r1, [fp, #-12]
    add r2, r0, r1
    str r2, [fp, #-44]
    ldr r0, [fp, #-44]
    str r0, [fp, #-4]
    b L3
L2:
L3:
    ldr r0, [fp, #-12]
    ldr r1, [fp, #-52]
    add r2, r0, r1
    str r2, [fp, #-48]
    ldr r0, [fp, #-48]
    str r0, [fp, #-12]
    b L0
L1:
    ldr r0, =str0
    bl __print_string
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
str0: .asciz "La suma de los números pares del 1 al 10 es:"
