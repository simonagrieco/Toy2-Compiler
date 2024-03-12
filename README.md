# Toy2-Compiler
Programming language definited for the Compiler Course from University of Salerno.

| Token       |           Lexeme         |
|-------------|:------------------------:|
| VAR         |           var            |
| ID          | \[A-Za-z_]\[A-Za-z0-9_]* |
| INTEGER     |         integer          |
| REAL        |           real           |
| STRING      |          string          |
| LPAR        |            (             |
| RPAR        |            )             |
| FUNC        |           func           |
| ENDFUNC     |         endfunc          |
| TYPERETURN  |            ->            |
| READ        |           <--            |
| WRITERETURN |           -->!           |
| WRITE       |           -->            |
| DOLLARSIGN  |            $             |
| OUT         |           out            |
| PROC        |           proc           |
| ENDPROC     |         endproc          |
| IF          |            if            |
| THEN        |           then           |
| ELSE        |           else           |
| ELSEIF      |          elseif          |
| DO          |            do            |
| ENDWHILE    |         endwhile         |
| ENDIF       |          endif           |
| PLUS        |            +             |
| MINUS       |            -             |
| DIV         |            /             |
| TIMES       |            *             |
| NEQ         |            <>            |
| EQ          |            =             |
| ASSIGN      |            ^=            |
| LT          |            <             |
| GT          |            >             |
| LE          |            <=            |
| GE          |            >=            |
| REF         |            @             |
| NOT         |            !             |
| OR          |           \|\|           |
| AND         |            &&            |
| SEMI        |            ;             |
| COLON       |            :             |
| COMMA       |            ,             |
| ENDVAR      |            \\            |
| TRUE        |           true           |
| FALSE       |          false           |

## Grammar
Program ::= IterProc Procedure Iter
IterProc ::= VarDecl IterProc
 | Function IterProc
 | empty 
 
Iter ::= VarDecl Iter
 | Function Iter
 | Procedure Iter
 | empty  
 
VarDecl ::= VAR Decls

Decls ::= Ids COLON Type SEMI Decls
 | Ids ASSIGN Consts SEMI Decls
 | Ids COLON Type SEMI ENDVAR 
 | Ids ASSIGN Consts SEMI ENDVAR 
 
Ids ::= ID COMMA Ids
 | ID
 
Consts ::= Const COMMA Consts
 | Const
 
Const ::= REAL_CONST
 | INTEGER_CONST
 | STRING_CONST
 | TRUE 
 | FALSE
 
Function ::= FUNCTION ID LPAR FuncParams RPAR TYPERETURN Types COLON 

Body ENDFUNCTION 

FuncParams ::= ID COLON Type OtherFuncParams
 |  empty 
 
OtherFuncParams ::= COMMA ID COLON Type OtherFuncParams
 |  empty 
 
Type ::= REAL
 | INTEGER
 | STRING
 | BOOLEAN
 
Types ::= Type COMMA Types
 | Type
 
Procedure ::= PROCEDURE ID LPAR ProcParams RPAR COLON Body ENDPROCEDURE

ProcParams::= ProcParamId COLON Type OtherProcParams
 | empty 
 
OtherProcParams ::= COMMA ProcParamId COLON Type OtherProcParams
 |  empty 
 
ProcParamId ::= ID
 | OUT ID
 
Body ::= VarDecl Body
 | Stat Body
 | empty 
 
Stat ::= Ids ASSIGN Exprs SEMI
 | ProcCall SEMI 
 | RETURN Exprs SEMI 
 | WRITE IOArgs SEMI 
 | WRITERETURN IOArgs SEMI
 | READ IOArgs SEMI
 | IfStat SEMI
 | WhileStat SEMI
 
FunCall ::= ID LPAR Exprs RPAR
 | ID LPAR RPAR
 
ProcCall ::= ID LPAR ProcExprs RPAR
 | ID LPAR RPAR
 
IfStat ::= IF Expr THEN Body Elifs Else ENDIF 
 
Elifs ::= Elif Elifs
 | empty 
 
Elif ::= ELIF Expr THEN Body

Else ::= ELSE Body
 | empty 
 
WhileStat ::= WHILE Expr DO Body ENDWHILE

IOArgs ::= IOConc IOArgs
 | DOLLARSIGN LPAR Expr RPAR IOArgs
 | empty  
 
IOConc ::= IOConc PLUS IOConc
 | STRING_CONST
 
ProcExprs::= Expr COMMA ProcExprs
 | REF ID COMMA ProcExprs
 | Expr
 | REF ID
 
Exprs ::= Expr COMMA Exprs
 | Expr
 
Expr ::= FunCall
 | REAL_CONST
 | INTEGER_CONST
 | STRING_CONST
 | ID
 | TRUE
 | FALSE
 | Expr PLUS Expr
 | Expr MINUS Expr
 | Expr TIMES Expr
 | Expr DIV Expr
 | Expr AND Expr
 | Expr OR Expr
 | Expr GT Expr
 | Expr GE Expr
 | Expr LT Expr
 | Expr LE Expr
 | Expr EQ Expr
 | Expr NE Expr
 | MINUS Expr %prec UMINUS
 | NOT Expr
 | LPAR Expr RPAR %prec ASSOC
