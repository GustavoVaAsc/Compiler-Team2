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


#### Motivation.


#### Objectives.


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


#### Grammar Derivation
One of the core components of a compiler is the parser, which validates and interprets the structure of source code based on a formal grammar. This section illustrates how our compiler applies derivation rules from a context-free grammar to parse a sample program.

For instance, given the following source code in our custom language:
```
import io;

class Perro {
    string nombre;

    void function ladrar() {
        println("Woof woof");
    }
}

void function main() {
    int8 x = 5 + 2;
    println(x);
}
```
Our compiler parses it and generates a leftmost derivation using the grammar defined in our parser module:
```
<Source> ::= <Libs> <TopDeclarations>Add commentMore actions
<Libs> ::= import <Id> ;
<Id> ::= io

<TopDeclarations> ::= <Class> <TopDeclarations>
<Class> ::= class <Id> <ClassBlock>
<Id> ::= Perro
<ClassBlock> ::= { <ClassMember> <ClassMember> }

<ClassMember> ::= <Variable> ;
<Variable> ::= <Type> <Id>
<Type> ::= <PrimitiveType>
<PrimitiveType> ::= string
<Id> ::= nombre

<ClassMember> ::= <Function>
<Function> ::= <Type> function <Id> ( ) <Block>
<Type> ::= <PrimitiveType>
<PrimitiveType> ::= void
<Id> ::= ladrar
<Block> ::= { <Statement> }
<Statement> ::= <FunctionCall> ;
<FunctionCall> ::= <Id> ( <Arguments> )
<Id> ::= println
<Arguments> ::= <Expression>
<Expression> ::= <Literal>
<Literal> ::= <String>
<String> ::= "Woof woof"

<TopDeclarations> ::= <Function>
<Function> ::= <Type> function <Id> ( ) <Block>
<Type> ::= <PrimitiveType>
<PrimitiveType> ::= void
<Id> ::= main
<Block> ::= { <Statement> <Statement> }

<Statement> ::= <Variable> ;
<Variable> ::= <Type> <Id> = <Expression>
<Type> ::= <PrimitiveType>
<PrimitiveType> ::= int8
<Id> ::= x
<Expression> ::= <Expression> <BinaryOp> <Expression>
<Expression> ::= <Literal>
<Literal> ::= <Integer>
<Integer>> ::= 5
<BinaryOp> ::= +
<Expression> ::= <Literal>
<Literal> ::= <Integer>
<Integer> ::= 2

<Statement> ::= <FunctionCall> ;
<FunctionCall> ::= <Id> ( <Arguments> )
<Id> ::= println
<Arguments> ::= <Expression>
<Expression> ::= <Id>
<Id> ::= x
```
## Results 
### Application start







## Conclusions




## Bibliography 
[1] C. Staff, “What is lexical analysis?,” *Coursera*, Apr. 10, 2024. Available: [https://www-coursera-org.translate.goog/articles/lexical-analysis?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=tc]

[2] “CS 340: Lecture 2: Finite Automata, Lexical Analysis”. GitHub Pages. [Online]. Available: [https://ycpcs.github.io/cs340-fall2016/lectures/lecture02.html]

[3] “Introduction of Finite Automata - GeeksforGeeks”. GeeksforGeeks.[Online]. Available: [https://www.geeksforgeeks.org/introduction-of-finite-automata/]

[4] “LR Parser”. GeeksforGeeks, Mar. 31, 2021.[Online]. Available: [https://www.geeksforgeeks.org/lr-parser/]

[5] “LALR Parser (with Examples)”. GeeksforGeeks, Jun. 24, 2021.[Online]. Available: [https://www.geeksforgeeks.org/lalr-parser-with-examples/]

[6] “Extended Backus–Naur form”. WIKIPEDIA.[Online]. Available: [https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form]

[7] “The Go Programming Language Specification - Language version go1.24”. GO, Dec. 30, 2024.[Online]. Available: [https://go.dev/ref/spec]
