package unam.fi.compilers.team2.parser

/*
---- GRAMMAR OF K* (EBNF) ----

<grammar>
    <Source> ::= <Libs> <TopDeclarations>
    <Libs> ::= { import <Id> }
    <TopDeclarations> ::= { <Declaration> | <Function> }
    <Declaration> ::= <Constant> | <Type> | <Variable>
    <Function> ::= <Type> function <FunctionName> (<Parameters>?) <Block>
    <Type> ::= <TypeName> <TypeId>; | <TypeName> <TypeId> = <TypeValue>;
    <Constant> ::= const <TypeName> <Id> = <ConstantValue>;
    <Variable> ::= <TypeName> <Id> = <Value>;
    <Parameters> ::= <TypeName> <Id> {, <TypeName> <Id> }
    <Block> ::= {<Statement>*}
    <Statement> ::= <Variable> ; | <FunctionCall>; | <OtherStatements>
    <FunctionCall> ::= <Id> (<Arguments>?) ;
    <Arguments> ::= <Expression> {, <Expression>}

    TODO: Finish the grammar
<\grammar>
*/

class Parser {
}