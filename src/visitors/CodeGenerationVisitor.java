package visitors;

import nodi.*;
import symbolTable.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CodeGenerationVisitor implements Visitor {

    SymbolTable symTable;
    public static String fileName;
    public static File outFile;
    static FileWriter fileWriter;

    public CodeGenerationVisitor(String fileName) {
        this.fileName = fileName;
    }

    public String convertTypeOp(String typeOp) {

        String type = "";
        if (typeOp.equals("PlusOp"))
            type = "+";
        if (typeOp.equals("MinusOp"))
            type = "-";
        if (typeOp.equals("TimesOp"))
            type = "*";
        if (typeOp.equals("DivOp"))
            type = "/";
        if (typeOp.equals("GTOp"))
            type = ">";
        if (typeOp.equals("GEOp"))
            type = ">=";
        if (typeOp.equals("LTOp"))
            type = "<";
        if (typeOp.equals("LEOp"))
            type = "<=";
        if (typeOp.equals("NEOp"))
            type = "!=";
        if (typeOp.equals("EQOp"))
            type = "==";
        if (typeOp.equals("AndOp"))
            type = "&&";
        if (typeOp.equals("OrOp"))
            type = "||";

        return type;
    }

    public String convertType(String type) {
        if (type.equals("integer")) type = "int";
        if (type.equals("integer_const")) type = "int";
        if (type.contains("boolean")) type = "bool";
        if (type.contains("boolean_const")) type = "bool";
        if (type.equals("string")) type = "char*";
        if (type.equals("string_const")) type = "char*";
        if (type.equals("real")) type = "float";
        if (type.equals("real_const")) type = "float";

        return type;
    }

    @Override
    public Object visit(BodyOp bodyOp) throws Exception {
        fileWriter.write("{\n");

        if (bodyOp.getVars() != null) {
            for (DeclOp decls : bodyOp.getVars()) {
                decls.accept(this);
            }
        }
        if (bodyOp.getStatOps() != null) {
            for (StatOp stats : bodyOp.getStatOps()) {
                stats.accept(this);
            }
        }
        fileWriter.write("}\n");
        return null;
    }

    @Override
    public Object visit(ConstOp constOp) throws Exception {
        if (constOp.getTypeConst().equals("integer_const")) {
            fileWriter.write(constOp.getLessema());
        }
        if (constOp.getTypeConst().equals("string_const")) {
            fileWriter.write('\"' + constOp.getLessema().replace("\n","\\n") + '\"');

        }
        if (constOp.getTypeConst().equals("real_const")) {
            fileWriter.write(constOp.getLessema());
        }
        if (constOp.getTypeConst().equals("boolean_const")) {
            if (constOp.getLessema() != null) {
                if (constOp.getLessema().equals("true")) {
                    fileWriter.write("true");
                }
            }
        }
        if (constOp.getTypeConst().equals("boolean_const")) {
            if (constOp.getLessema() != null) {
                if (constOp.getLessema().equals("false")) {
                    fileWriter.write("false");
                }
            }
        }

        return null;
    }

    @Override
    public Object visit(DeclOp declOp) throws Exception {

        if (declOp.getType() != null) {
            String tipo = convertType(declOp.getType().toString());
        }

        ArrayList<Identifier> idList = declOp.getIds();
        ArrayList<ConstOp> constList = declOp.getConsts();


        //Inizializzazione con costanti
        if (declOp.getType() == null) {
            for (int i = 0; i < idList.size() && i < constList.size(); i++) {
                Identifier id = idList.get(i);
                ConstOp const1 = constList.get(i);
                if (const1.getTypeConst().contains("string")) {
                    String tipo = convertType(const1.getTypeConst());
                    fileWriter.write(tipo);
                    fileWriter.write(" ");
                    id.accept(this);
                    fileWriter.write(" = (char*) malloc(MAXCHAR);\n"); //<-- vedere!
                    id.accept(this);
                    fileWriter.write(" = strncpy(");
                    id.accept(this);
                    fileWriter.write(",");
                    const1.accept(this);
                    fileWriter.write(", MAXCHAR)");
                } else {
                    String tipo = convertType(const1.getTypeConst());
                    fileWriter.write(tipo);
                    fileWriter.write(" ");
                    id.accept(this);
                    fileWriter.write(" = ");
                    const1.accept(this);
                }
                fileWriter.write(";\n");
            }
        }

        // Inizializzazione con tipo
        else {
            for (int i = 0; i < idList.size(); i++) {
                declOp.getType().accept(this);
                fileWriter.write(" ");
                idList.get(i).accept(this);
                if (declOp.getType().getTypeVar().contains("string")) {
                    fileWriter.write(" = (char*) malloc(MAXCHAR)"); //<-- vedere! (char*)
                }
                fileWriter.write(";\n");
            }
        }
        return null;
    }

    @Override
    public Object visit(ElifOp elifOp) throws Exception {
        symTable = elifOp.getSymbolTable();

        fileWriter.write("else if(");
        elifOp.getExpr().accept(this);
        fileWriter.write(") ");
        elifOp.getBody().accept(this);

        return null;
    }

    @Override
    public Object visit(ExprOp exprOp) throws Exception {
        //vedere per parentesi

        if (exprOp instanceof ConstOp || exprOp instanceof UnaryOp || exprOp instanceof Identifier || exprOp instanceof FunCallOp) {
            return exprOp.accept(this);
        }

        String type = String.valueOf(exprOp);
        //String operationType = convertTypeOp(type);

        //Controlla se l'operazione avviene su stringhe
        boolean isStringOperation = exprOp.getExpr1().getTypeOp().contains("string") || exprOp.getExpr2().getTypeOp().contains("string");

        if (type.equals("PlusOp")) {
            if (isStringOperation) {
                fileWriter.write("str_concat(");
                if (!exprOp.getExpr1().getTypeOp().contains("string")) {
                    getTypeConversion("string", exprOp.getExpr1());
                } else {
                    exprOp.getExpr1().accept(this);
                }
                fileWriter.write(",");
                if (!exprOp.getExpr2().getTypeOp().contains("string")) {
                    getTypeConversion("string", exprOp.getExpr2());
                } else {
                    exprOp.getExpr2().accept(this);
                }
                fileWriter.write(")");

            } else {
                exprOp.getExpr1().accept(this);
                fileWriter.write("+");
                exprOp.getExpr2().accept(this);
            }
        } else if (type.equals("MinusOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write("-");
            exprOp.getExpr2().accept(this);
        } else if (type.equals("TimesOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write("*");
            exprOp.getExpr2().accept(this);
        } else if (type.equals("DivOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write("/");
            exprOp.getExpr2().accept(this);
        } else if (type.equals("AndOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write("&&");
            exprOp.getExpr2().accept(this);
        } else if (type.equals("OrOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write("||");
            exprOp.getExpr2().accept(this);
        } else if (type.equals("EQOp")) {
            //comparazione di stringhe
            if (isStringOperation) {
                fileWriter.write("strcmp(");
                exprOp.getExpr1().accept(this);
                fileWriter.write(",");
                exprOp.getExpr2().accept(this);
                fileWriter.write(")==0");
            } else {
                exprOp.getExpr1().accept(this);
                fileWriter.write("==");
                exprOp.getExpr2().accept(this);
            }
        } else if (type.equals("NEOp")) {
            //comparazione di stringhe
            if (isStringOperation) {
                fileWriter.write("strcmp(");
                exprOp.getExpr1().accept(this);
                fileWriter.write(",");
                exprOp.getExpr2().accept(this);
                fileWriter.write(")!=0");
            } else {
                exprOp.getExpr1().accept(this);
                fileWriter.write("!=");
                exprOp.getExpr2().accept(this);
            }
        } else if (type.equals("GTOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write(">");
            exprOp.getExpr2().accept(this);
        } else if (type.equals("GEOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write(">=");
            exprOp.getExpr2().accept(this);
        } else if (type.equals("LTOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write("<");
            exprOp.getExpr2().accept(this);
        } else if (type.equals("LEOp")) {
            exprOp.getExpr1().accept(this);
            fileWriter.write("<=");
            exprOp.getExpr2().accept(this);
        }

        return null;
    }

    @Override
    public Object visit(FunCallOp funCallOp) throws Exception {
        funCallOp.getId().accept(this);
        fileWriter.write("(");

        if (funCallOp.getExprOps() != null) {
            if (funCallOp.getExprOps().size() > 1) {
                for (int i = 0; i < funCallOp.getExprOps().size() - 1; i++) {
                    funCallOp.getExprOps().get(i).accept(this);
                    fileWriter.write(", ");
                }
            }
            //Non inserisco la virgola
            if (!funCallOp.getExprOps().isEmpty()) {
                funCallOp.getExprOps().get(funCallOp.getExprOps().size() - 1).accept(this);
            }
        }

        fileWriter.write(")");
        return null;
    }

    String returnFuncName; //prende il nome della funzione che ha più parametri in uscita

    @Override
    public Object visit(FuncNodeOp funcNodeOp) throws Exception {
        symTable = funcNodeOp.getSymbolTable();

        RowTable res = symTable.lookup(funcNodeOp.getId().getLessema());
        TypeField.TypeFieldFunction decl = (TypeField.TypeFieldFunction) res.getType();
        ArrayList<String> typesList = decl.getOutputParam();

        /*if (typesList.size() > 1) {
            //creazione della struct per le funzioni che hanno più tipi di ritorno
            try {
                fileWriter.write("typedef struct {\n");
                for (int i = 0; i < typesList.size(); i++) {
                    TypeOp type = funcNodeOp.getTypes().get(i);
                    fileWriter.write("\t");
                    type.accept(this);
                    fileWriter.write(" value" + i + ";\n");
                }
                fileWriter.write("} ");
                funcNodeOp.getId().accept(this);
                fileWriter.write("Struct;\n\n");

                returnFuncName = funcNodeOp.getId().toString() + "Struct";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } */

        if (typesList.size() > 1) {
            funcNodeOp.getId().accept(this);
            fileWriter.write("Struct ");
            funcNodeOp.getId().accept(this);
        } else {
            funcNodeOp.getTypes().get(0).accept(this); //un solo
            fileWriter.write(" ");
            funcNodeOp.getId().accept(this);
        }

        fileWriter.write(" (");
        //inserimento parametri
        if (funcNodeOp.getFuncParams().size() > 1) {
            for (int i = 0; i < funcNodeOp.getFuncParams().size() - 1; i++) {
                funcNodeOp.getFuncParams().get(i).accept(this);
                fileWriter.write(", ");
            }
        }
        //ultimo parametro (togliere la virgola)
        if (!funcNodeOp.getFuncParams().isEmpty())
            funcNodeOp.getFuncParams().get(funcNodeOp.getFuncParams().size() - 1).accept(this);
        fileWriter.write(")");

        //Inserisco il body
        funcNodeOp.getBody().accept(this);


        //returnFuncName = "";

        return null;
    }

    @Override
    public Object visit(FuncParamsOp funcParamsOp) throws Exception {

        funcParamsOp.getTypeOp().accept(this);
        fileWriter.write(" ");
        funcParamsOp.getId().accept(this);

        return null;
    }

    @Override
    public Object visit(Identifier identifier) throws Exception {

        symTable.lookup(identifier.getLessema());

        boolean isRefVariable = rtProc.contains(identifier.getLessema()); //true se il lessama di is è presente nell'array dei valori in ref

        // Aggiungo il simbolo "*" solo se la variabile è passata per riferimento
        if (isRefVariable) {
            fileWriter.write("*");
        } else if (identifier.getAttributo() != null) {
            if (identifier.getAttributo().equals("out")) {
                fileWriter.write("*");
            }
        }

        // Scrivo il nome della variabile
        fileWriter.write(identifier.getLessema());

        /*if ((identifier.getAttributo() != null && identifier.getAttributo().equals("out"))) {
            fileWriter.write("*");
            fileWriter.write(identifier.getLessema());
        } else {
            fileWriter.write(identifier.getLessema());
        } */

        return null;
    }

    @Override
    public Object visit(IfStatOp ifStatOp) throws Exception {
        symTable = ifStatOp.getSymbolTableIfBody();

        fileWriter.write("if(");
        ifStatOp.getExpr().accept(this);
        fileWriter.write(")");

        ifStatOp.getBody().accept(this);

        for (ElifOp elifs : ifStatOp.getElifs()) {
            elifs.accept(this);
        }

        if (ifStatOp.getElsebody() != null) {
            fileWriter.write("else");
            symTable = ifStatOp.getSymbolTableElsebody();
            ifStatOp.getElsebody().accept(this);
            symTable = ifStatOp.getSymbolTableElsebody().getFather();
        }
        return null;
    }

    @Override
    public Object visit(IterOp iterOp) throws Exception {
        //lista decl, procnode, funcnode
        if (iterOp.getVars() != null) {
            for (DeclOp d : iterOp.getVars()) {
                d.accept(this);
            }
        }
        if (iterOp.getListfuncs() != null) {
            for (FuncNodeOp f : iterOp.getListfuncs()) {
                f.accept(this);
            }
        }
        if (iterOp.getListprocs() != null) {
            for (ProcNodeOp p : iterOp.getListprocs()) {
                p.accept(this);
            }
        }

        return null;
    }

    @Override
    public Object visit(ProcCallOp procCallOp) throws Exception {
        procCallOp.getId().accept(this);
        fileWriter.write("(");

        if (procCallOp.getProcExprOps() != null) {
            if (procCallOp.getProcExprOps().size() > 1) {
                for (int i = 0; i < procCallOp.getProcExprOps().size() - 1; i++) {
                    //controllo passaggio per riferimento
                    if (procCallOp.getProcExprOps().get(i) instanceof Identifier identifier && identifier.getAttributo() != null && identifier.getAttributo().equals("ref")) {
                        fileWriter.write("&");
                    }
                    procCallOp.getProcExprOps().get(i).accept(this);
                    fileWriter.write(", ");
                }
            }

            //Non inserisco la virgola
            if (!procCallOp.getProcExprOps().isEmpty()) {
                if (procCallOp.getProcExprOps().get(procCallOp.getProcExprOps().size() - 1) instanceof Identifier identifier && identifier.getAttributo() != null && identifier.getAttributo().equals("ref"))
                    fileWriter.write("&");
                procCallOp.getProcExprOps().get(procCallOp.getProcExprOps().size() - 1).accept(this);
            }
        }

        fileWriter.write(");\n");
        return null;
    }

    ArrayList<String> rtProc = new ArrayList<>(); //lista dei parametri ref (usato per il controllo degli *)

    @Override
    public Object visit(ProcNodeOp procNodeOp) throws Exception {

        symTable = procNodeOp.getSymbolTable();

        //Prendo tutti i parametri che sono passati per riferimento
        for (RowTable rt : symTable.getListRow()) {
            if (rt.getProperties().equals("ref")) {
                rtProc.add(rt.getSymbol());
            }
        }

        if (!procNodeOp.getId().getLessema().equals("main")) {
            //firma procedura
            fileWriter.write("void ");
            procNodeOp.getId().accept(this);
            fileWriter.write(" (");
        } else {
            fileWriter.write("void main(int argc, char *argv[]");
        }

        //inseriamo i parametri
        if (procNodeOp.getProcParamOps().size() > 1) {
            for (int i = 0; i < procNodeOp.getProcParamOps().size() - 1; i++) {
                procNodeOp.getProcParamOps().get(i).accept(this);
                fileWriter.write(", ");
            }

        }
        //Non inserisco la virgola perché è l'ultimo parametro
        if (!procNodeOp.getProcParamOps().isEmpty()) {
            procNodeOp.getProcParamOps().get(procNodeOp.getProcParamOps().size() - 1).accept(this);
            fileWriter.write(")");
        }
        if (procNodeOp.getProcParamOps().isEmpty()) {
            fileWriter.write(")");
        }
        if (procNodeOp.getBody() != null) {
            procNodeOp.getBody().accept(this);
        }

        rtProc.clear(); //pulisco l'array per prendermi i valori ref per il prossimo scope

        return null;
    }

    @Override
    public Object visit(ProcParamsOp procParamsOp) throws Exception {
        procParamsOp.getType().accept(this);
        fileWriter.write(" ");
        procParamsOp.getId().accept(this);
        return null;
    }

    @Override
    public Object visit(ProgramOp programOp) throws Exception {
        symTable = programOp.getSymbolTable();

        String path = "test_files" + File.separator + "c_out" + File.separator;


        if (!Files.exists(Path.of(path)))
            new File(path).mkdirs();

        File file = new File(path  + this.fileName + ".c");

        file.createNewFile();

        fileWriter = new FileWriter(file);

        //inserimento librerie di base C
        baseLibreries();
        fileWriter.write("\n");
        //inserimento funzioni di supporto
        helperFunctions();
        fileWriter.write("\n");
        creaStrutturaFirme(programOp);
        fileWriter.write("\n");
        programOp.getIter().accept(this);
        fileWriter.write("\n");
        fileWriter.close();

        return null;
    }

    int index = 0;

    @Override
    public Object visit(StatOp statOp) throws Exception {
        //gestire return, write, writereturn, read, assign

        boolean funcCallHandled = false;
        int j = 0;
        if (statOp.getLessema() != null && statOp.getLessema().equals("assign")) {
            if (statOp.getIds() != null && statOp.getExprOps() != null) {

                for (Identifier id : statOp.getIds()) {
                    for (ExprOp e : statOp.getExprOps()) {
                        //caso in cui chiamiamo una funzione con più assegnamenti
                        if (statOp.getIds().size() > 1) {
                            if ((e instanceof FunCallOp)) {
                                String idfunc = String.valueOf(((FunCallOp) e).getId());
                                if (!funcCallHandled) {
                                    fileWriter.write(idfunc + "Struct" + " " + idfunc + "Str" + index + "= ");
                                    ((FunCallOp) e).getId().accept(this);
                                    fileWriter.write("(");

                                    if (((FunCallOp) e).getExprOps() != null) {
                                        if (!((FunCallOp) e).getExprOps().isEmpty()) {
                                            for (ExprOp param : ((FunCallOp) e).getExprOps()) {
                                                param.accept(this);
                                                // Verifica se il parametro corrente è l'ultimo
                                                if (param != ((FunCallOp) e).getExprOps().get(((FunCallOp) e).getExprOps().size() - 1)) {
                                                    fileWriter.write(", "); // Non inserire la virgola se non è l'ultimo parametro
                                                }
                                            }
                                        }
                                    }
                                    fileWriter.write(");\n");
                                    funcCallHandled = true;
                                }

                                id.accept(this);
                                fileWriter.write("= " + idfunc + "Str" + index + "." + "value" + j + ";" + "\n");
                                j++;
                            }
                        }
                        //caso base
                        else {
                            id.accept(this);
                            fileWriter.write("=");
                            e.accept(this);
                            fileWriter.write(";\n");
                        }
                    }
                }
            }
        }


        index++;
        int k = 0;
        if (statOp.getLessema().equals("return")) {
            if (statOp.getExprOps() != null) {
                //caso in cui si ha una funzione con più parametri di ritorno (aggiungo nome struct come return e tutti i valori)
                if (statOp.getExprOps().size() > 1) {
                    fileWriter.write(returnFuncName + " " + returnFuncName + ";" + "\n");
                    for (ExprOp e : statOp.getExprOps()) {
                        fileWriter.write(returnFuncName + "." + "value" + k + " = ");
                        k++;
                        e.accept(this);
                        fileWriter.write(";\n");
                    }
                    fileWriter.write("return ");
                    fileWriter.write(returnFuncName);
                    fileWriter.write(";\n");
                } else { //caso base
                    for (ExprOp e : statOp.getExprOps()) {
                        fileWriter.write("return ");
                        e.accept(this);
                        fileWriter.write(";\n");
                    }
                }
            }
        }

        if (statOp.getLessema() != null && statOp.getLessema().equals("write")) {

            if (!statOp.getExprOps().isEmpty()) {
                fileWriter.write("printf(");
                //controllo sui tipi
                for (int i = 0; i < statOp.getExprOps().size(); i++) {
                    ExprOp expr = statOp.getExprOps().get(i);
                    // Prima espressione
                    //statOp.getExprOps().get(0).accept(this);

                    if(expr instanceof ConstOp constOp){
                        expr.accept(this);
                        if(expr.getTypeOp().equals("string_const")){
                            fileWriter.write(" \" "+constOp.getLessema()+"\"");
                        }
                    }

                    else if (expr instanceof Identifier id) {
                        RowTable idRow = symTable.lookup(id.getLessema());
                        //id.accept(this);
                        if (idRow != null) {

                            String type = idRow.getType().toString();

                            if (type.contains("integer")) {
                                fileWriter.write("\"%d\", ");
                            }
                            if (type.contains("string")) {
                                fileWriter.write("\"%s\", ");
                            }
                            if (type.contains("real")) {
                                fileWriter.write("\"%f\", ");
                            }
                            id.accept(this);
                            if (id != (statOp.getExprOps().get(statOp.getExprOps().size() - 1))) {
                                fileWriter.write(", "); // Non inserire la virgola se non è l'ultimo parametro
                            }
                        }
                    }

                    else if(expr instanceof FunCallOp funCallOp){

                        //fileWriter.write(", ");

                        RowTable res = symTable.lookup(funCallOp.getId().getLessema());
                        TypeField.TypeFieldFunction decl = (TypeField.TypeFieldFunction) res.getType();
                        ArrayList<String> typesList = decl.getOutputParam();
                        if (typesList != null) {
                            if (typesList.contains("integer")) {
                                fileWriter.write("\"%d \", ");
                            }
                            if (typesList.contains("string")) {
                                fileWriter.write("\"%s \", ");
                            }
                            if (typesList.contains("real")) {
                                fileWriter.write("\"%f \", ");
                            }
                        }
                        expr.accept(this);
                    }

                    else{
                        if (expr.getTypeOp() != null) {
                            String type = expr.getTypeOp();

                            if (type.contains("integer")) {
                                fileWriter.write("\"%d \", ");
                            }
                            if (type.contains("string")) {
                                fileWriter.write("\"%s \", ");
                            }
                            if (type.contains("real")) {
                                fileWriter.write("\"%f \", ");
                            }
                        }
                        statOp.getExprOps().get(0).accept(this);
                    }


                }


                fileWriter.write(");\n");
            }
        }

        if (statOp.getLessema() != null && statOp.getLessema().equals("writereturn")) {
            fileWriter.write("printf(");

            //controllo sui tipi
            for (int i = 0; i < statOp.getExprOps().size(); i++) {
                ExprOp expr = statOp.getExprOps().get(i);

                if(expr instanceof ConstOp constOp){
                    RowTable idRow = symTable.lookup(((ConstOp) expr).getLessema());

                    expr.accept(this);

                    if(expr.getTypeOp().equals("string_const")){
                        fileWriter.write(" \""+constOp.getLessema()+"\"");
                    }
                }
                // Se l'espressione è un ID
                else if (expr instanceof Identifier id) {
                    RowTable idRow = symTable.lookup(id.getLessema());
                    if (idRow != null) {

                        String type = idRow.getType().toString();

                        if (type.contains("integer")) {
                            fileWriter.write("\"%d \", ");
                        }
                        if (type.contains("string")) {
                            fileWriter.write("\"%s \", ");
                        }
                        if (type.contains("real")) {
                            fileWriter.write("\"%f \", ");

                        }
                        expr.accept(this);
                        if (id != (statOp.getExprOps().get(statOp.getExprOps().size() - 1))) {
                            fileWriter.write(", "); // Non inserire la virgola se non è l'ultimo parametro
                        }
                    }
                }

                else if(expr instanceof FunCallOp funCallOp){
                    //fileWriter.write(", ");
                    RowTable res = symTable.lookup(funCallOp.getId().getLessema());
                    TypeField.TypeFieldFunction decl = (TypeField.TypeFieldFunction) res.getType();
                    ArrayList<String> typesList = decl.getOutputParam();
                    if (typesList != null) {
                        if (typesList.contains("integer")) {
                            fileWriter.write("\"%d\", ");
                        }
                        if (typesList.contains("string")) {
                            fileWriter.write("\"%s\", ");
                        }
                        if (typesList.contains("real")) {
                            fileWriter.write("\"%f\", ");
                        }
                    }
                    expr.accept(this); //VEDI BENE!!!!
                }

                else{
                    if (expr.getTypeOp() != null) {
                        String type = expr.getTypeOp();

                        if (type.contains("integer")) {
                            fileWriter.write("\"%d \", ");
                        }
                        if (type.contains("string")) {
                            fileWriter.write("\"%s \", ");
                        }
                        if (type.contains("real")) {
                            fileWriter.write("\"%f \", ");
                        }
                    }
                    statOp.getExprOps().get(0).accept(this);
                }
            }


            //Non inserisco la virgola
            /*if (!statOp.getExprOps().isEmpty())
                statOp.getExprOps().get(statOp.getExprOps().size() - 1).accept(this); */
            if (statOp.getLessema().equals("writereturn")) {
                if (!statOp.getExprOps().isEmpty())
                    fileWriter.write(",");
                fileWriter.write(" \"\\n\");\n");
            } else
                fileWriter.write(");\n");
        }


        if (statOp.getLessema() != null && statOp.getLessema().equals("read")) {
            ArrayList<ExprOp> exprs = statOp.getExprOps();

            String checkn;

            if (exprs != null && !exprs.isEmpty()) {
                for (int i = 0; i < exprs.size(); i++) {
                    ExprOp currentExpr = exprs.get(i);
                    if (currentExpr instanceof Identifier id) {

                        fileWriter.write("scanf(\"");
                        RowTable idRow = symTable.lookup(id.getTypeOp());

                        if (idRow != null) {
                            String tipo = idRow.getType().toString();
                            if (tipo.contains("integer")) {
                                fileWriter.write("%d\", ");
                                fileWriter.write("&");
                                currentExpr.accept(this);
                            } else if (tipo.contains("real")) {
                                fileWriter.write("%f\", ");
                                fileWriter.write("&");
                                currentExpr.accept(this);
                            } else if (tipo.contains("string")) {
                                fileWriter.write("%s\", ");
                                currentExpr.accept(this);
                            }
                        }
                        //fileWriter.write(");\n");
                    } else {
                        fileWriter.write("printf(");
                        statOp.getExprOps().get(i).accept(this);

                    }
                    fileWriter.write(" );\n");
                }
            }
        }


        if (statOp instanceof WhileOp) {
            statOp.accept(this);
        }

        if (statOp instanceof ProcCallOp) {
            statOp.accept(this);
        }

        if (statOp instanceof IfStatOp) {
            statOp.accept(this);
        }

        if (statOp instanceof ElifOp) {
            statOp.accept(this);
        }
        return null;
    }

    private String replaceNewline(String input) {
        return input.replace("\\n", "\\\\n");
    }


    @Override
    public Object visit(TypeOp typeOp) throws Exception {
        String type = typeOp.getTypeVar();
        if (type.equals("real")) {
            fileWriter.write("float");
        } else if (type.equals("integer")) {
            fileWriter.write("int");
        } else if (type.equals("string")) {
            fileWriter.write("char*");
        } else if (type.equals("boolean")) {
            fileWriter.write("bool");
        } else {
            throw new RuntimeException("Il tipo non esiste!");
        }
        return null;
    }

    @Override
    public Object visit(UnaryOp unaryOp) throws Exception {
        String type = String.valueOf(unaryOp);
        //String type = unaryOp.getType();
        if (type.equals("UminusOp")) {
            fileWriter.write("-");
            fileWriter.write("(");
            unaryOp.getExpr().accept(this);
            fileWriter.write(")");
        } else if (type.equals("NotOp")) {
            fileWriter.write("!");
            unaryOp.getExpr().accept(this);
        }
        return null;
    }

    @Override
    public Object visit(WhileOp whileOp) throws Exception {
        symTable = whileOp.getSymbolTable();
        fileWriter.write("while(");
        whileOp.getExpr().accept(this);
        fileWriter.write(") ");
        whileOp.getBody().accept(this);
        return null;
    }

    private void getTypeConversion(String targetType, ExprOp expr) throws Exception {
        if (targetType.equals("string")) {
            if (expr.getTypeOp().contains("integer")) {
                fileWriter.write("integer_to_str(");
                expr.accept(this);
                fileWriter.write(")");
            } else if (expr.getTypeOp().contains("real")) {
                fileWriter.write("real_to_str(");
                expr.accept(this);
                fileWriter.write(")");
            } else if (expr.getTypeOp().contains("boolean")) {
                fileWriter.write("bool_to_str(");
                expr.accept(this);
                fileWriter.write(")");
            } else {
                // Se già una stringa, accetta
                expr.accept(this);
            }
        }
    }

    //metodi librerie e funzioni di base
    public void baseLibreries() throws IOException {
        fileWriter.write("#include <stdio.h>\n");
        fileWriter.write("#include <stdlib.h>\n");
        fileWriter.write("#include <string.h>\n");
        fileWriter.write("#include <math.h>\n");
        fileWriter.write("#include <unistd.h>\n");
        fileWriter.write("#include <stdbool.h>\n");
        fileWriter.write("#define MAXCHAR 512\n");
    }

    public void helperFunctions() throws IOException {
        fileWriter.write("char* integer_to_str(int i){\n");
        fileWriter.write("int length= snprintf(NULL,0,\"%d\",i);\n");
        fileWriter.write("char* result= (char*) malloc(length+1);\n"); //<-- vedere! (char*)
        fileWriter.write("snprintf(result,length+1,\"%d\",i);\n");
        fileWriter.write("return result;}\n");
        fileWriter.write("\n");

        fileWriter.write("char* real_to_str(float i){\n");
        fileWriter.write("int length= snprintf(NULL,0,\"%f\",i);\n");
        fileWriter.write("char* result= (char*) malloc(length+1);\n"); //<-- vedere! (char*)
        fileWriter.write("snprintf(result,length+1,\"%f\",i);\n");
        fileWriter.write("return result;}\n");
        fileWriter.write("\n");

        fileWriter.write("char* char_to_str(char i){\n");
        fileWriter.write("int length= snprintf(NULL,0,\"%c\",i);\n");
        fileWriter.write("char* result= (char*) malloc(length+1);\n"); //<-- vedere! (char*)
        fileWriter.write("snprintf(result,length+1,\"%c\",i);\n");
        fileWriter.write("return result;}\n");
        fileWriter.write("\n");

        fileWriter.write("char* bool_to_str(bool i){\n");
        fileWriter.write("int length= snprintf(NULL,0,\"%d\",i);\n");
        fileWriter.write("char* result= (char*) malloc(length+1);\n"); //<-- vedere! (char*)
        fileWriter.write("snprintf(result,length+1,\"%d\",i);\n");
        fileWriter.write("return result;}\n");
        fileWriter.write("\n");

        fileWriter.write("char* str_concat(char* str1, char* str2){\n");
        fileWriter.write("char* result= (char*) malloc(sizeof(char)* MAXCHAR);\n"); //<-- vedere! (char*)
        fileWriter.write("result=strcat(result,str1);\n");
        fileWriter.write("result=strcat(result,str2);\n");
        fileWriter.write("return result;}\n");
        fileWriter.write("\n");

        fileWriter.write("char* read_str(){\n");
        fileWriter.write("char* str= (char*) malloc(sizeof(char)* MAXCHAR);\n"); //<-- vedere! (char*)
        fileWriter.write("scanf(\"%s\",str);\n");
        fileWriter.write("return str;}\n");
        fileWriter.write("\n");


        fileWriter.write("int str_to_bool(char* expr){\n");
        fileWriter.write("int i=0;\n");
        fileWriter.write("if ((strcmp(expr, \"true\")==0) || (strcmp(expr, \"1\"))==0 )\n");
        fileWriter.write("i=1;\n");
        fileWriter.write("if ((strcmp(expr, \"false\")==0) || (strcmp(expr, \"0\"))==0 )\n");
        fileWriter.write("i=0;\n");
        fileWriter.write("return i;}\n");

    }

    public void creaStrutturaFirme(ProgramOp programOp) throws Exception {
        IterOp iter = programOp.getIter();

        if (iter != null) {

            //creazione strutture
            if (iter.getListfuncs() != null) {
                for (FuncNodeOp func : iter.getListfuncs()) {
                    String firma = creaStruttura(func);
                }
            }

            // Raccolta firme di funzioni
            if (iter.getListfuncs() != null) {
                for (FuncNodeOp func : iter.getListfuncs()) {
                    String firma = creaFirmaFunzione(func);
                }
            }

            // Raccolta firme di procedure
            if (iter.getListprocs() != null) {
                for (ProcNodeOp proc : iter.getListprocs()) {
                    String firma = creaFirmaProcedura(proc);
                }
            }
        }
    }

    private String creaFirmaProcedura(ProcNodeOp procNodeOp) throws Exception {
        symTable = procNodeOp.getSymbolTable();

        // Se il nome della procedura è "main", non creare la firma
        if (!procNodeOp.getId().getLessema().equals("main")) {
            fileWriter.write("void ");
            procNodeOp.getId().accept(this);
            fileWriter.write(" (");

            // Inserisci i parametri
            if (procNodeOp.getProcParamOps().size() > 1) {
                for (int i = 0; i < procNodeOp.getProcParamOps().size() - 1; i++) {
                    procNodeOp.getProcParamOps().get(i).accept(this);
                    fileWriter.write(", ");
                }
            }

            // Ultimo parametro (senza virgola)
            if (!procNodeOp.getProcParamOps().isEmpty()) {
                procNodeOp.getProcParamOps().get(procNodeOp.getProcParamOps().size() - 1).accept(this);
            }

            fileWriter.write(");\n");
        }

        return null;
    }

    private String creaFirmaFunzione(FuncNodeOp funcNodeOp) throws Exception {
        symTable = funcNodeOp.getSymbolTable();

        RowTable res = symTable.lookup(funcNodeOp.getId().getLessema());
        TypeField.TypeFieldFunction decl = (TypeField.TypeFieldFunction) res.getType();
        ArrayList<String> typesList = decl.getOutputParam();


        if (typesList.size() <= 1) {
            String tipo = convertType(typesList.get(0));
            fileWriter.write(tipo + " ");
            funcNodeOp.getId().accept(this);
            fileWriter.write(" (");
        }
        if (typesList.size() > 1) {
            fileWriter.write(funcNodeOp.getId() + "Struct" + " ");
            funcNodeOp.getId().accept(this);
            fileWriter.write(" (");
        }

        // Inserisci i parametri
        if (funcNodeOp.getFuncParams().size() > 1) {
            for (int i = 0; i < funcNodeOp.getFuncParams().size() - 1; i++) {
                funcNodeOp.getFuncParams().get(i).accept(this);
                fileWriter.write(", ");
            }
        }


        // Ultimo parametro (senza virgola)
        if (!funcNodeOp.getFuncParams().isEmpty()) {
            funcNodeOp.getFuncParams().get(funcNodeOp.getFuncParams().size() - 1).accept(this);
        }


        fileWriter.write(");\n");
        return null;
    }

    private String creaStruttura(FuncNodeOp funcNodeOp) throws Exception {
        symTable = funcNodeOp.getSymbolTable();

        RowTable res = symTable.lookup(funcNodeOp.getId().getLessema());
        TypeField.TypeFieldFunction decl = (TypeField.TypeFieldFunction) res.getType();
        ArrayList<String> typesList = decl.getOutputParam();

        if (typesList.size() > 1) {
            //creazione della struct per le funzioni che hanno più tipi di ritorno
            try {
                fileWriter.write("typedef struct {\n");
                for (int i = 0; i < typesList.size(); i++) {
                    TypeOp type = funcNodeOp.getTypes().get(i);
                    fileWriter.write("\t");
                    type.accept(this);
                    fileWriter.write(" value" + i + ";\n");
                }
                fileWriter.write("} ");
                funcNodeOp.getId().accept(this);
                fileWriter.write("Struct;\n\n");

                returnFuncName = funcNodeOp.getId().toString() + "Struct";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
