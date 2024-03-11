package main;
import java_cup.runtime.*;
%%

%public
%class Toy2Lexer
%unicode //Inisieme dei caratteri su cui lo scanner lavorerà, leggendo da un file
         //di testo e necessario che venga abilitato
%cup //Si usa per interfacciarsi con un parser generato da CUP!
%cupsym sym
%line
%column


%{

    StringBuffer string = new StringBuffer();
    private Symbol symbol(int typeOp) {
        return new Symbol(typeOp, yyline, yycolumn);
    }
    private Symbol symbol(int typeOp, Object value) {
        return new Symbol(typeOp, yyline, yycolumn, value);
    }

    private Symbol installID(String lessema){
            Symbol token;

            token=symbol(sym.ID,lessema);
            return token;
        }
%}



identificatore=[$_A-Za-z][$_A-Za-z0-9]*
intLiteral =  [0-9]+ (e-?[0-9]+)? | 0x[0-9a-f] | 0b[01]+
realLiteral = [0-9]+ \. [0-9]+ (e-?[0-9]+)?

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
whitespace     = {LineTerminator} | [ \t\f]



%state STRING
%state CHAR
%state INLINE_COMMENT
%state BLOCK_COMMENT

%%

//Constanti
<YYINITIAL> {intLiteral} {return symbol(sym.INTEGER_CONST, yytext());}
<YYINITIAL> {realLiteral} {return symbol(sym.REAL_CONST, yytext());}
<YYINITIAL> \"  { string.setLength(0); yybegin(STRING); }

//Parole Chiavi
<YYINITIAL> "var" {return symbol(sym.VAR);}
<YYINITIAL> "true" {return symbol(sym.TRUE);}
<YYINITIAL> "false" {return symbol(sym.FALSE);}
<YYINITIAL> "real" {return symbol(sym.REAL);}
<YYINITIAL> "integer" {return symbol(sym.INTEGER);}
<YYINITIAL> "string" {return symbol(sym.STRING);}
<YYINITIAL> "boolean" {return symbol(sym.BOOLEAN);}
<YYINITIAL> "return" {return symbol(sym.RETURN);}
<YYINITIAL> "func" {return symbol(sym.FUNCTION);}
<YYINITIAL> "endfunc" {return symbol(sym.ENDFUNCTION);}
<YYINITIAL> "proc" {return symbol(sym.PROCEDURE);}
<YYINITIAL> "endproc" {return symbol(sym.ENDPROCEDURE);}
<YYINITIAL> "out" {return symbol(sym.OUT);}
<YYINITIAL> "$" {return symbol(sym.DOLLARSIGN);}
<YYINITIAL> "if" {return symbol(sym.IF);}
<YYINITIAL> "else" {return symbol(sym.ELSE);}
<YYINITIAL> "then" {return symbol(sym.THEN);}
<YYINITIAL> "endif" {return symbol(sym.ENDIF);}
<YYINITIAL> "elseif" {return symbol(sym.ELIF);}
<YYINITIAL> "while" {return symbol(sym.WHILE);}
<YYINITIAL> "do" {return symbol(sym.DO);}
<YYINITIAL> "endwhile" {return symbol(sym.ENDWHILE);}
<YYINITIAL> "\\" {return symbol(sym.ENDVAR);}
<YYINITIAL> "@" {return symbol(sym.REF);}


//Parole
<YYINITIAL> {identificatore} {return installID(yytext());}


//Commenti
<YYINITIAL> "%" {string.setLength(0); yybegin(BLOCK_COMMENT); }


//Operatori Relazionali
<YYINITIAL> "=" {return symbol(sym.EQ);}
<YYINITIAL> "<>" {return symbol(sym.NE);}
<YYINITIAL> "<" {return symbol(sym.LT);}
<YYINITIAL> "<=" {return symbol(sym.LE);}
<YYINITIAL> ">" {return symbol(sym.GT);}
<YYINITIAL> ">=" {return symbol(sym.GE);}


//Operatori Aritmetici
<YYINITIAL> "+" {return symbol(sym.PLUS);}
<YYINITIAL> "-" {return symbol(sym.MINUS);}
<YYINITIAL> "*" {return symbol(sym.TIMES);}
<YYINITIAL> "/" {return symbol(sym.DIV);}


//Operatori Logici
<YYINITIAL> "&&" {return symbol(sym.AND);}
<YYINITIAL> "||" {return symbol(sym.OR);}
<YYINITIAL> "!" {return symbol(sym.NOT);}



//Separatori
<YYINITIAL> ":" {return symbol(sym.COLON);}
<YYINITIAL> ";" {return symbol(sym.SEMI);}
<YYINITIAL> "," {return symbol(sym.COMMA);}
<YYINITIAL> "(" {return symbol(sym.LPAR);}
<YYINITIAL> ")" {return symbol(sym.RPAR);}



//Input, Output, Assign ecc...
<YYINITIAL> "^=" {return symbol(sym.ASSIGN);}
<YYINITIAL> "->" {return symbol(sym.TYPERETURN);}
<YYINITIAL> "-->" {return symbol(sym.WRITE);}
<YYINITIAL> "-->!" {return symbol(sym.WRITERETURN);}
<YYINITIAL> "<--" {return symbol(sym.READ);}


<YYINITIAL> {whitespace} {/*no action and no return*/}

/* error fallback */
<YYINITIAL> [^] { throw new Error("Illegal character <"+yytext()+">"); }
<<EOF>> { return null; }

//state
<STRING> {
      \"                             { yybegin(YYINITIAL);
                                       return symbol(sym.STRING_CONST,
                                       string.toString()); }
      [^\n\r\"\\]+                   { string.append( yytext() ); }
      \\t                            { string.append('\t'); }
      \\n                            { string.append('\n'); }

      \\r                            { string.append('\r'); }
      \\\"                           { string.append('\"'); }
      \\                             { string.append('\\'); }
     <<EOF>>  {throw new Error("Stringa non chiusa!"); }
}

//state
<BLOCK_COMMENT> {
      \%  { yybegin(YYINITIAL);}
      [^\%]+  { }
      <<EOF>>  {throw new Error("Commento non chiuso!"); }
}



