### **National Autonomous University of Mexico** <br>
### **School of Engineering**
### **Compilers**

**TEAM MEMBERS:**  
- 320068234  
- 320239126  
- 320257599  
- 320278107  
- 117002029  

**Group:** 05  
**Semester:** 2025-II  

**Mexico City, Mexico. June 2025.**


# Compilers-Team2
Compiler repository for the subject of compilers (Faculty of Engineering, semester 2025-2)

## Introduction
#### Problem statement.
>  This project proposes the development of an app composed of a lexical analyzer, a syntax analyzer, and a compiler, using the knowledge acquired during theoretical classes.
First, the lexical analyzer will aim to correctly identify and classify the basic lexical elements (tokens) present in the source code, such as identifiers, keywords, and operators. Then, the syntax analyzer will be responsible for validating the grammatical structure of the code, ensuring it follows the syntactic rules of the defined language.
Finally, the compiler will seek to translate the validated source code into an executable representation, enabling its correct interpretation and execution by a machine or a specific runtime environment.
With this project, we aim to consolidate the key concepts of the compilation process, from source code analysis to its translation and execution.

#### Motivation.
> The creation of the lexical analyzer, syntax analyzer, and compiler will allow for a comprehensive understanding of the compiler construction process from identifying the lexical elements of the source code to its final translation. This project will provide an opportunity to unify and practically apply the theoretical concepts discussed in class, helping to deepen the understanding of each stage of the compilation process and highlight its importance in the development of programming languages.

#### Objectives.
> To develop a system composed of a lexical analyzer, a syntax analyzer, and a compiler that can identify and classify tokens, verify the syntactic structure of the source code, and translate it into an executable format, applying the theoretical knowledge acquired in class to gain a practical understanding of how a complete compiler works.

## Theoretical Framework

**_Lexical Analyzer:_**  
A lexical analyzer is a program that converts the input text into tokens and classifies these tokens into predefined categories.  
The lexical analyzer plays a crucial role in the analysis phase, as it provides the tokens to the parser to facilitate syntax analysis.

**_Token:_**  
A token is a sequence of characters grouped into a single entity. Each token represents a set of character sequences that convey a specific meaning.  
**Classification:**
- Keywords  
- Identifiers  
- Literals  
- Operators  
- Punctuation  
- Special characters  
- Constants

**_Lexical Errors:_**  
A lexical error happens when the lexer finds an invalid sequence of characters that cannot be classified as an actual token.  
These errors tend to happen due to exceeding length of numeric constants, identifiers that are way too long, illegal characters, and others.

**_Finite Automata:_**  
A finite automaton is an abstract machine that recognizes patterns by processing symbols step by step, transitioning between a finite number of states. It determines if the input should be accepted or rejected based on the final state it reaches.

**_Finite Automata in Token Recognition:_**  
Because the lexical structure of most programming languages can be described using a regular language, lexical analyzers often rely on finite automata to identify valid tokens. This is achieved by defining regular expressions for all possible tokens, and then transforming them into a finite automaton (usually a deterministic finite automaton).

**_Parser:_**  
A parser is a program that analyzes the syntactic structure of a given text based on a formal grammar. It receives tokens from the lexical analyzer and organizes them into a parse tree or syntax tree, representing the hierarchical syntactic structure of the source code.

**_Abstract Syntax Tree (AST):_**  
An Abstract Syntax Tree is a hierarchical tree representation of the abstract syntactic structure of source code. Unlike a concrete syntax tree, the AST omits syntax-specific details such as parentheses and semicolons, focusing on the logical structure of the code. It is used by compilers and interpreters during semantic analysis and code generation to reason about program constructs like expressions, statements, functions, and control structures.

**_Three Address Code (TAC):_**  
Three Address Code is an intermediate representation used in compilers. Each instruction in TAC involves at most three operands and represents a simple operation, typically in the form: `x = y op z`. It is more abstract than assembly but closer to machine code than high-level source code. TAC simplifies the process of optimization and code generation by breaking complex statements into basic operations.

**_Compiler:_**  
A compiler translates a program written in a high-level programming language (e.g., C, C++, COBOL) into machine language. A compiler typically generates assembly language first and then translates the assembly into machine code. A utility known as a linker combines all the necessary machine language modules into an executable program.

**_Assembly Language:_**  
Assembly language is a low-level programming language that provides a symbolic representation of a computer’s machine code instructions. Each assembly instruction corresponds closely to a single machine instruction, making it suitable for fine-tuned hardware-level programming. In the compilation process, high-level language code is often translated into assembly before being converted into machine code.

## Body
### KStar Language
The custom language implemented in this compiler is mostly based on C, but it modifies several features to align with the goals of our compiler design. It supports basic programming structures such as functions, classes, conditionals, and loops, along with a limited but sufficient set of data types: Integer, Real (float), String, and Boolean.
However, the language includes a few important restrictions to maintain clarity and reduce complexity:
* Functions cannot receive parameters. They can only be declared and contain internal logic, but no arguments are passed to them.
* Functions and classes can be declared but not called. Their presence is structural and used to demonstrate syntax validity.
* No support for arrays, input statements, or memory-related features like pointers.
* All data must be initialized and handled directly within the program without runtime input.
* The language enforces static typing using the supported datatypes listed above.
* If instruction could not handle "else if"
* Variables that start with 't' cannot be declared
* Expressions of kind (a == b && b != c || !d ... ) are not supported

These constraints simplify both parsing and evaluation, allowing the focus to remain on the core concepts of language structure, syntax rules, and compilation stages.

### Lexer
The lexical analyzer was implemented in the ```Kotlin``` language and designed to run within an Android application. Its main goal is to process a sequence of lines of source code and transform them into a structured list of lexical tokens, each with its category, line, and column of appearance. It removes comments and applies regular expressions to identify and classify lexical tokens. It uses external resources (```Keywords.txt``` and ```Datatypes.txt```) to define the valid keywords and data types of the language. This provides flexibility, as it allows the behavior to be easily modified or extended by changing the token definitions in the configuration files, enabling the supported language to be updated without altering the lexer’s code.
The output consists of a list of Token objects containing detailed information about their type and position, facilitating the work of the parser.

To perform tokenization, the regular expressions we used to identify different elements in the source code was the following:
* Identifiers: [a-zA-Z_][a-zA-Z0-9_]*
* Operators:>>=|<<=|\+=|-=|\*=|/=|%=|&&|\|\||\+\+|--|&=|\|=|\^=|=|!|\+|-|\*|/|%|&|\||\^
* Relational symbols: ==|!=|>=|<=|>|<
* Punctuation marks: \*|\(|\)|\.|,|:|;|\{|\}|->
* Boolean values: \b(true|false)\b
* String literals: "([^"\\]|\\.)*"
* Floating-point numbers: -?\d+\.\d+
* Integers: -?\d+
Below are images that demonstrate the lexer's functionality:

> Tests with code snippets that do not produce any errors in the parser. Example 1:
![Test 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20night.jpg)
![Test 2 - Lexer](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-Lexer%202%20(fixed).jpg)
---
> Example 2
<br><br><br>
![Test 3](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%204%20night.jpg)
![Test 3 - Lexer](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20-Lexer%203%20(fixed).jpg)


### Parser
Our parser was implemented using a top-down recursive descent approach, where each method represents a grammar rule defined for the language. This strategy allows for a clean, readable implementation tightly aligned with the grammar structure, making it ideal for ```LL(1)``` parsers.

The parser receives as input the list of tokens generated by the lexical analyzer (Lexer) and produces a hierarchical structure of ```AST``` (Abstract Syntax Tree) nodes that represents the syntactic structure of the source code.

Each function corresponds to a specific grammar rule. For instance:

* ```parseProgram()``` starts the parsing process by calling ```parseDeclaration()``` repeatedly.
* ```parseDeclaration()``` determines whether a class, function, or statement is being parsed and delegates accordingly.
* ```parseExpression()``` handles complex expressions by combining operators, groupings, literals, identifiers, and parentheses.

Additionally, the parser implements a derivation logging system (log, logToken) to track how grammar rules are applied step by step. This helps during debugging and offers visual support for understanding how the parser operates internally.

Error handling is also a key feature; the ```error()``` method provides precise feedback by pointing to the line and token that caused the issue, improving the debugging experience for users.

To better illustrate the parser's behavior and its relationship with the language grammar, we include an example of a complete derivation based on a sample input. This derivation reflects how the parser applies context-free grammar rules step by step to analyze and validate code structure.

#### Grammar Derivation
One of the core components of a compiler is the parser, which validates and interprets the structure of source code based on a formal grammar. This section illustrates how our compiler applies derivation rules from a context-free grammar to parse a sample program.

For instance, given the following source code in our custom language:
```
// Bark function
class Perro{
	string nombre;
	function void ladrar (){
		writeln("Woof woof");
	}
}

// Function that adds two numbers
function void main (){
	int x = 5 + 2;
	writeln(x);
}
```
Our compiler parses it and generates a leftmost derivation using the grammar defined in our parser module:
```
Program -> Declaration*

Declaration -> ClassDeclaration

ClassDeclaration -> 'class' Identifier '{' Declaration* '}'

Identifier -> Perro

Declaration -> Statement

Statement -> VariableDeclaration

VariableDeclaration -> Datatype Identifier ['=' Expression] ';'

Datatype -> string

Identier -> nombre

Declaration -> FunctionDeclaration

FunctionDeclaration -> 'function' Datatype Identifier '(' ')' '{' Statement* '}'

Datatype -> void

Identifier -> ladrar

Statement -> PrintStatement

PrintStatement -> 'writeln' '(' Expression ')' ';'

Expression -> Assigment

Literal -> "Woof woof"

Declaration -> FunctionDeclaration

FunctionDeclaration -> 'function' Datatype Identifier '(' ')' '{' Statement* '}'

Datatype -> void

Identifier -> main

Statement -> VariableDeclaration

VariableDeclaration -> Datatype Identifier ['=' Expression] ';'

Datatype -> int

Identifier -> x

Expression -> Assigment

Literal -> 5

Operator -> +

Literal -> 2

Statement -> PrintStatement

PrintStatement -> 'writeln' '(' Expression ')' ';'

Expression -> Assigment

Identifier -> x
```
<br><br>
Below are images that demonstrate the parser's functionality, including both successful cases and situations where errors occur, allowing us to observe how the parser detects them.

> Tests with code snippets that do not produce any errors in the parser. Example 1:
![Test 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20night.jpg)
![Test 2 - Parser](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-Parser%202.jpg)
![Test 2 - Parser Pt2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-Parser%202.1.jpg)
---

> Example 2
<br><br><br>
![Test 3](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%204%20night.jpg)
![Test 3 - Parser](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20-Parser%203.jpg)
![Test 3 - Parser Pt.2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20-Parser%203.1.jpg)

> Tests with code snippets that produce errors in the parser.
> <br><br><br>
![Fail 1](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%201.1.jpg)
![Fail 1 - Message](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%201.2.jpg)
<br>

**What’s wrong?** <br>
The names are written incorrectly, which causes the parsing to fail since the grammar does not recognize the following misspelled terms:

* `str1ng` instead of `string`
* `vo1d` instead of `void`
* `funct1on` instead of `function`
* `1nt` instead of `int`
---
<br><br>

![Fail 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%202.1.jpg)
![Fail 2 - Message](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%202.2.jpg)
 <br>

**What’s wrong?** <br>
The keyword while is misspelled, so the grammar does not recognize it and the parser fails.

* ```wh1le``` instead of ```while```
<br>

### Semantic Analyzer
The semantic analyzer was implemented in ```Kotlin``` and is responsible for performing semantic validation of the ```Abstract Syntax Tree``` (AST) generated by the parser. Its main role is to ensure that the code complies with the language rules, including type validation and the correct use of variables, functions, classes, and control structures.
The analyzer uses a symbol table to track declared identifiers (variables, functions, classes) across different scopes. It performs checks to detect errors such as:
* Redefinition of identifiers
* Use of undeclared variables
* Type incompatibilities in assignments, expressions, and return statements
* Invalid conditions in control structures such as if, while, and for

In addition, the analyzer infers types in expressions and verifies type compatibility using helper methods. Errors are collected with precise line numbers to facilitate debugging.
The output consists of a list of detailed semantic error messages.

Below are images that demonstrate the functionality of the semantic analyzer, including both successful cases and situations where errors occur, allowing us to observe how the semantic analyzer detects them.

>Tests with code snippets that do not produce any errors in the semantic analyzer. Example 1:
![Test 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20night.jpg)
![Test 2 - Semantic Analyzer](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-Parser%202.1.jpg)
---
> Example 2:
<br><br><br>

![Test 3](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%204%20night.jpg)
![Test 3 - Semantic Analyzer](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20-Parser%203.1.jpg)

> Tests with code snippets that produce errors in the semantic analyzer.
![Fail 1](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%201.1.jpg)
![Fail 1 - Parsing Completed](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%201.2.jpg)
> ![Fail 1 - Message](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%201.3.jpg)
<br>

**What’s wrong?** <br>
The function that adds two numbers is declared as void; however, it attempts to return x, which is an integer value. Although the parser does not detect any errors, the code is semantically incorrect because a function declared as void should not return any value.

<br><br>

![Fail 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%202.1%20(fixed).jpg)
![Fail 2 - Parsing Completed](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%202.2.jpg)
![Fail 2 - Message](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%202.3.jpg)
<br>

**What’s wrong?** <br>
The variable i is declared as an integer (```int```); however, a string (```"Hola"```) was assigned to it. Although this does not produce errors during parsing, it is semantically incorrect because the assigned data type does not match the declared type.
<br>

### Compiler
When the user requests compilation, the entered code is internally processed through a lexical and syntactic analyzer that transforms the text into an Abstract Syntax Tree (AST). This tree is then translated into an intermediate representation, which translates into two types of target code: ARMv7 assembly or a simulated Bytecode we created to execute the program in the aoo. These instructions (Bytecode) are interpreted by a stack-based virtual machine (StackVM), which simulates the execution of the program.

This virtual machine manages registers, local variables, and a call stack to enable function execution. It also supports arithmetic and logical operations, conditional jumps, type conversions, and data output.

The execution result is produced as a text string, which displays the final output to the user. This modular design clearly separates responsibilities between editing, compilation, and result visualization.

Below are images that demonstrate the functionality of the compiler, including both successful cases and situations where errors occur, allowing us to observe how the compiler works.

>Tests with code snippets that do not produce any errors in the compiler.
![Test 1](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%201%20Night.jpg)
![Test 1 - Compiler](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%201%20-%20Compile.jpg)
---
<br><br>

![Test 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20night.jpg)
![Test 2 - Compiler](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-%20Compile.jpg)


> Tests with code snippets that produce errors in the compiler.
![Fail 1](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%201.1.jpg)
![Fail 1 - Compile Error](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Fail%201-%20Error%20Message%20.jpg)
---
<br><br>
![Fail 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Fail%202-%20Compile%20Error.jpg)
![Fail 2 - Compile Error](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Fail%202-%20Error%20Message.jpg)

## Results 
### Application start
It was decided to create an application due to the ease of access it offers, as well as being intuitive, easy to use, and visually appealing to the user.
Once downloaded, the application appears on the user’s device with the following icon and name:
> App icon <br>
![App icon](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/App.jpg)

Upon launching the application, the main interface is displayed. This environment includes a text area for entering source code and three primary buttons that correspond to the key functions of the compiler: Lex, Parse, and Compile.

The application also allows users to switch between dark mode and light mode, depending on their visual preference.
> Dark mode <br>
![Dark mode](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/UI%20Night.jpg)
<br> <br>
> Light mode <br>
![Light mode](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/UI%20Light.jpg)

The three core options Lex, Parse, and Compile are accessible via buttons:. Depending on the selected button, the application performs the corresponding process.
> Buttons <br>
![Buttons](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Buttons.jpg)

>### Lex
Upon selecting Lex, the application performs lexical analysis on the input code. The lexer reads the program text character by character to identify valid lexemes, classifying them into tokens such as:
* Identifiers
* Keywords
* Operators
* Literals
* Punctuation

This process verifies whether the code contains symbols allowed by the KStar language. If correct, it outputs a stream of tokens with metadata like line and column numbers.
<br> _Note: Even if the lexical analysis succeeds, syntactic or semantic errors may still occur in later stages._


>### Parse
The Parse button takes the tokens produced by the lexer and compares them against the grammar rules of the KStar language. If the structure of the code conforms to the rules (such as proper nesting, punctuation, and ordering), the parser builds a syntax tree.

If errors are detected, they are flagged with details indicating the location and nature of the error.


>### Compile
When selected, the Compile button uses code that has already passed lexical and syntactic analysis and simulates its execution.

At this stage, the application checks the logic of the program and runs it according to the language rules, succesfully simulating the execution of the code. 

To avoid errors, the code must include complete instructions ready to be executed like assignments, loops, and function calls.

> For further guidance on how to use the application and its features, please refer to the attached user manual ([User Manual](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Compiler_Manual.pdf))
<br>

> ### ARMv7 Assembly Code Generation
<br>
The final phase of the compiler involves transforming the intermediate code representation into ARMv7 Assembly instructions, a low-level instruction set commonly used in mobile devices and embedded systems. This step simulates how a real compiler prepares a program for execution on hardware by translating high-level logic into hardware-level instructions.

The TargetCodeGenerator class, implemented in Kotlin, receives a list of intermediate instructions and produces a string of equivalent ARMv7 code. These instructions include assignments, arithmetic operations (both binary and unary), conditional and unconditional jumps, print statements, and return instructions.

To handle variable storage, the generator calculates offsets within the current stack frame. Each user-defined function begins by saving the execution context (pushing frame and return registers), allocates space on the stack, and restores the previous state at the end. It also includes support routines such as _software_divide for division and _print_string for displaying text.

This process completes the compilation flow, showing how the original source code, after lexical, syntactic, and semantic analysis, is translated into a fully executable representation.
<br>

> Why do we separate our Bytecode VM simulation from the ARM Assembly instructions?
<br>
Although generating ARMv7 code helps us simulate how real hardware would execute the program, it's difficult to run and visualize that code directly on Android devices. To address this, we also implemented a Bytecode Virtual Machine (StackVM) within the app to simulate program behavior and provide immediate feedback. This dual system allows us to meet our target of compiling down to real hardware-level instructions while still keeping the process interactive and educational for users.
<br>

> #### Assembly Simulation

> ### What is CPUlator? <br>
CPUlator is an online simulator that allows you to run and debug programs on architectures such as ARMv7 or MIPS without the need for physical hardware. 

> ### How does it work in general? <br>

>#### Hardware simulation:
<br>
CPUlator mimics how a real ARMv7 processor would work, including registers, memory, interrupts, and instruction execution.
 <br>

>#### Source code:
<br>
You can write programs in Assembly language or C.

>#### Compilation and execution:
<br>
The simulator compiles and runs your code step by step, showing you which instructions are being executed and how the values in the registers and memory change.

>#### Visual debugging:
It has an interface where you can see:
* The processor’s registers (such as R0, R1...).
* The contents of RAM memory.
* The instructions being executed.
* Options to step through the code, reset the system, or run the entire program.

To test and run the generated assembly code, we used the following online ARM simulator:
[Simulator](https://cpulator.01xz.net/?sys=arm)

The sample programs used to test the compiler can be found in the following folder of the repository:
[Test](https://github.com/GustavoVaAsc/Compiler-Team2/tree/main/app/src/main/assets/ASMOutputs)

## Conclusion
With the developed application, it was possible to apply, unify, and implement in practice all the concepts covered in the course, from the construction of the lexical analyzer to the final creation of the compiler. This process not only reinforced the theoretical knowledge acquired in class but also helped to understand the relationship between each stage of the compilation process and its importance in the development of programming languages.
Furthermore, implementing each component within a single application made it easier to visualize and understand the complete flow followed by a compiler, from token identification to executable code generation. In summary, the completion of the project made it possible to bridge theory and practice for the correct execution of the created app.

## Bibliography 
[1] C. Staff, “What is lexical analysis?,” *Coursera*, Apr. 10, 2024. Available: [https://www-coursera-org.translate.goog/articles/lexical-analysis?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=tc]

[2] “CS 340: Lecture 2: Finite Automata, Lexical Analysis”. GitHub Pages. [Online]. Available: [https://ycpcs.github.io/cs340-fall2016/lectures/lecture02.html]

[3] “Introduction of Finite Automata - GeeksforGeeks”. GeeksforGeeks.[Online]. Available: [https://www.geeksforgeeks.org/introduction-of-finite-automata/]

[4] “LR Parser”. GeeksforGeeks, Mar. 31, 2021.[Online]. Available: [https://www.geeksforgeeks.org/lr-parser/]

[5]"Recursive Descent Parser", GeeksforGeeks, Feb. 5, 2025. [Online]. Available: [https://www.geeksforgeeks.org/recursive-descent-parser/]

[6] “Extended Backus–Naur form”. WIKIPEDIA.[Online]. Available: [https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form]

[7] “The Go Programming Language Specification - Language version go1.24”. GO, Dec. 30, 2024.[Online]. Available: [https://go.dev/ref/spec]

[8] Neso Academy.“LL(1) Parsing - Solved Problems (Set 1)”. Mar. 30, 2023.[Online Video]. Available: [https://www.youtube.com/watch?v=5P7ehgZ6EIs]

[9] Neso Academy, "Problem of Left Recursion and Solution in CFGs", YouTube. Aug. 25, 2022. [Online]. Available: [https://www.youtube.com/watch?v=IO5ie7GbJGI]

[10] "Abstract Syntax Tree (AST) in Java", GeeksforGeeks, Aug. 12, 2021. [Online]. Available: [https://www.geeksforgeeks.org/abstract-syntax-tree-ast-in-java/]

[11] "Semantic Analysis in Compiler Design", GeeksforGeeks, Apr. 22, 2020. [Online]. Available: [https://www.geeksforgeeks.org/semantic-analysis-in-compiler-design/]

[12] "Documentation – Arm Developer". [Online]. Available: [https://developer.arm.com/documentation]

[13] "Kotlin Programming Language", Kotlin. [Online]. Available: [https://kotlinlang.org/]

[14] ConsoleTVs, "VirtualMachine/src/vm.cpp at master · ConsoleTVs/VirtualMachine", GitHub.  [Online]. Available: [https://github.com/ConsoleTVs/VirtualMachine/blob/master/src/vm.cpp]

[15] "Phases of a Compiler", GeeksforGeeks, Jan. 25, 2025. [Online]. Available: [https://www.geeksforgeeks.org/phases-of-a-compiler/]

