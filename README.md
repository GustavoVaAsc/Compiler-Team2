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


# Lexer-Compilers-Team2
Compiler repository for the subject of compilers (Faculty of Engineering, semester 2025-2)

## Introduction
#### Problem statement.
This project proposes the development of an app composed of a lexical analyzer, a syntax analyzer, and a compiler, using the knowledge acquired during theoretical classes.
First, the lexical analyzer will aim to correctly identify and classify the basic lexical elements (tokens) present in the source code, such as identifiers, keywords, and operators. Then, the syntax analyzer will be responsible for validating the grammatical structure of the code, ensuring it follows the syntactic rules of the defined language.
Finally, the compiler will seek to translate the validated source code into an executable representation, enabling its correct interpretation and execution by a machine or a specific runtime environment.
With this project, we aim to consolidate the key concepts of the compilation process, from source code analysis to its translation and execution.

#### Motivation.
The creation of the lexical analyzer, syntax analyzer, and compiler will allow for a comprehensive understanding of the compiler construction process—from identifying the lexical elements of the source code to its final translation. This project will provide an opportunity to unify and practically apply the theoretical concepts discussed in class, helping to deepen the understanding of each stage of the compilation process and highlight its importance in the development of programming languages.

#### Objectives.
To develop a system composed of a lexical analyzer, a syntax analyzer, and a compiler that can identify and classify tokens, verify the syntactic structure of the source code, and translate it into an executable format, applying the theoretical knowledge acquired in class to gain a practical understanding of how a complete compiler works.

## Theoretical framework 
_Lexical Analyzer:_ A lexical analyzer is a program that converts the input text into tokens and classifies these tokens into predefined categories. 
The lexical analyzer plays a crucial role in the analysis phase, as it provides the tokens to the parser to facilitate syntax analysis.

_Token:_ A token is a sequence of characters grouped into a single entity. Each token represents a set of character sequences that convey a specific meaning.\
Classification:
* Keywords
* Identifiers
* Literals
* Operators
* Punctuation
* Special characters
*  Constants
  
_Lexical Errors:_ A lexical error happens when the lexer finds an invalid sequence of characters that cannot be classified as an actual token.
These errors tend to happen due to exceeding length of numeric constants, identifiers that are way too long, illegal characters, and others.

_Finite Automata:_ A finite automata is an abstract machine that recognizes patterns by processing symbols step by step, transitioning between
a finite number of states. It determines if this input should be accepted or rejected based on the final state it reaches.

_Finite Automata in Token Recognition:_ Because the lexical structure of most programming languages can be described using a regular language,
lexical analyzers often rely on finite automata to identify valid tokens. This is achieved by defining regular expressions for all possible tokens, 
and then transforming them into a finite automaton (usually a deterministic finite automaton).


## Body

---
### Lexer
Lexer's functionality ---------- (PENDIENTE)

Below are images that demonstrate the lexer's functionality, including successful cases and situations where errors occur.

> Tests with code snippets that do not produce any errors in the parser.
![Test 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20night.jpg)
![Test 2 - Lexer](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-Lexer%202.jpg)





![Test 3](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20night.jpg)
![Test 3 - Lexer](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20-Lexer%203.jpg)


> Tests with code snippets that produce errors in the parser.
-------------- (PENDIENTE)----------------------------------------------------------------------

### Parser
Parser's functionality ---------- (PENDIENTE)

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
<br>
Below are images that demonstrate the parser's functionality, including both successful cases and situations where errors occur, allowing us to observe how the parser detects them.

> Tests with code snippets that do not produce any errors in the parser.
![Test 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20night.jpg)
![Test 2 - Parser](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-Parser%202.jpg)
![Test 2 - Parser Pt2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-Parser%202.1.jpg)





![Test 3](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20night.jpg)
![Test 3 - Parser](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20-Parser%203.jpg)
![Test 3 - Parser Pt.2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20-Parser%203.1.jpg)

> Tests with code snippets that produce errors in the parser.
![Fail 1](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%201.1.jpg)
![Fail 1 - Message](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%201.2.jpg)
 <br>
What’s wrong?
The names are written incorrectly, which causes the parsing to fail since the grammar does not recognize the following misspelled terms:

* `str1ng` instead of `string`
* `vo1d` instead of `void`
* `funct1on` instead of `function`
* `1nt` instead of `int`
<br>

![Fail 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%202.1.jpg)
![Fail 2 - Message](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Parsing%20Error%202.2.jpg)
 <br>
What’s wrong?
The keyword while is misspelled, so the grammar does not recognize it and the parser fails.

* ‘wh1le’ instead of ‘while’
<br>

### Semantic Analyzer
Semantic Analyzer's functionality ---------- (PENDIENTE)

Below are images that demonstrate the functionality of the semantic analyzer, including both successful cases and situations where errors occur, allowing us to observe how the semantic analyzer detects them.

>Tests with code snippets that do not produce any errors in the semantic analyzer.
![Test 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20night.jpg)
![Test 2 - Semantic Analyzer](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%202%20-Parser%202.1.jpg)





![Test 3](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20night.jpg)
![Test 3 - Semantic Analyzer](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Test%203%20-Parser%203.1.jpg)

> Tests with code snippets that produce errors in the semantic analyzer.
![Fail 1](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%201.1.jpg)
![Fail 1 - Parsing Completed](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%201.2.jpg)
> ![Fail 1 - Message](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%201.3.jpg)
<br>
 The function that adds two numbers is declared as void; however, it attempts to return x, which is an integer value. Although the parser does not detect any errors, the code is semantically incorrect because a function declared as void should not return any value.
<br>

![Fail 2](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%202.1.jpg)
![Fail 2 - Parsing Completed](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%202.2.jpg)
![Fail 2 - Message](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Semantic%20Error%202.3.jpg)
<br>
 The variable i is declared as an integer (int); however, a string ("Hola") was assigned to it. Although this does not produce errors during parsing, it is semantically incorrect because the assigned data type does not match the declared type.
<br>

### Compiler
Compiler's functionality ---------- (PENDIENTE)------------------------------

Below are images that demonstrate the functionality of the compiler, including both successful cases and situations where errors occur, allowing us to observe how the compiler works.

>Tests with code snippets that do not produce any errors in the compiler.
---------------------------(PENDIENTE)---------------------------------------------------

> Tests with code snippets that produce errors in the compiler.
---------------------------(PENDIENTE)---------------------------------------------------

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


> Light mode <br>
![Light mode](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/UI%20Light.jpg)

The three core options—Lex, Parse, and Compile—are accessible via buttons:. Depending on the selected button, the application performs the corresponding process.
> Buttons <br>
![Buttons](https://github.com/GustavoVaAsc/Compiler-Team2/blob/main/app/src/main/assets/Screenshots/Buttons.jpg)

>### Lex

Upon selecting Lex, the application performs lexical analysis on the input code. The lexer reads the program text character by character to identify valid lexemes, classifying them into tokens such as:
*Identifiers
*Keywords
*Operators
*Literals
*Punctuation

This process verifies whether the code contains symbols allowed by the defined language. If correct, it outputs a stream of tokens with metadata like line and column numbers.
_Note:_ Even if the lexical analysis succeeds, syntactic or semantic errors may still occur in later stages.

>### Parse

The Parse button takes the tokens produced by the lexer and compares them against the grammar of the language. If the structure of the code conforms to the rules (such as proper nesting, punctuation, and ordering), the parser builds a syntax tree.

If errors are detected, they are flagged with details indicating the location and nature of the error.

>### Compile

-----COMPLETAR RESULTS CON MANUAL--

## Conclusions
With the developed application, it was possible to apply, unify, and implement in practice all the concepts covered in the course, from the construction of the lexical analyzer to the final creation of the compiler. This process not only reinforced the theoretical knowledge acquired in class but also helped to understand the relationship between each stage of the compilation process and its importance in the development of programming languages.
Furthermore, implementing each component within a single application made it easier to visualize and understand the complete flow followed by a compiler, from token identification to executable code generation. In summary, the completion of the project made it possible to bridge theory and practice for the correct execution of the created app.

## Bibliography 
[1] C. Staff, “What is lexical analysis?,” *Coursera*, Apr. 10, 2024. Available: [https://www-coursera-org.translate.goog/articles/lexical-analysis?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=tc]

[2] “CS 340: Lecture 2: Finite Automata, Lexical Analysis”. GitHub Pages. [Online]. Available: [https://ycpcs.github.io/cs340-fall2016/lectures/lecture02.html]

[3] “Introduction of Finite Automata - GeeksforGeeks”. GeeksforGeeks.[Online]. Available: [https://www.geeksforgeeks.org/introduction-of-finite-automata/]

[4] “LR Parser”. GeeksforGeeks, Mar. 31, 2021.[Online]. Available: [https://www.geeksforgeeks.org/lr-parser/]

[5] “LALR Parser (with Examples)”. GeeksforGeeks, Jun. 24, 2021.[Online]. Available: [https://www.geeksforgeeks.org/lalr-parser-with-examples/]

[6] “Extended Backus–Naur form”. WIKIPEDIA.[Online]. Available: [https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form]

[7] “The Go Programming Language Specification - Language version go1.24”. GO, Dec. 30, 2024.[Online]. Available: [https://go.dev/ref/spec]

[8] Neso Academy.“LL(1) Parsing - Solved Problems (Set 1)”. Mar. 30, 2023.[Online Video]. Available: [https://www.youtube.com/watch?v=5P7ehgZ6EIs]

