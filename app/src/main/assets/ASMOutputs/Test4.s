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
    str r0, [fp, #-4]
    ldr r0, [fp, #-4]
    ldr r1, [fp, #-16]
    cmp r0, r1
    movge r2, #1
    movlt r2, #0
    str r2, [fp, #-12]
    ldr r0, [fp, #-12]
    ldr r1, [fp, #-24]
    cmp r0, r1
    moveq r2, #1
    movne r2, #0
    str r2, [fp, #-20]
    ldr r0, [fp, #-20]
    cmp r0, #0
    bne L0
    ldr r0, =str0
    bl __print_string
    b L1
L0:
    ldr r0, =str1
    bl __print_string
L1:

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
str0: .asciz "Hot :("
str1: .asciz "Cold :p"
