package visitors;

import nodi.*;
import symbolTable.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


//Type Checking & Type System
public class TypeVisitor implements Visitor {

    private SymbolTable currentScope;
    ArrayList<String> returnTypes = new ArrayList<>(); //controllo tipi di ritorno nel return caso -> return a, b, c; --> tipi di a,b,c
    ArrayList<String> returnExpr = new ArrayList<>(); //controllo tipi in output della funzione -> ...func() -> a, b, c --> prendi tipi a,b,c
    ArrayList<String> paramsFunc = new ArrayList<>(); //lista degli  parametri funzione (id)
    boolean returnBodyFunc; //bool per il controllo di almeno un return in func
    private static final String[] combinazioniBoolean = {"boolean", "boolean", "boolean"}; //operazioni: AND e OR

    private static final String[][] combinazioniRelop = {{"integer", "integer", "boolean"}, //operazioni: <,<=,>,>=,=,<>
            {"real", "real", "boolean"},
            {"integer", "real", "boolean"},
            {"real", "integer", "boolean"},
            {"string", "string", "boolean"},
            {"boolean", "boolean", "boolean"}};

    private static final String[][] combinazioniOperatori = {{"integer", "integer", "integer"}, //operazioni: +,-,*,/
            {"integer", "real", "real"},
            {"real", "integer", "real"},
            {"real", "real", "real"}};

    private static final String[][] combinazioniMinus = {{"integer", "integer"},
            {"real", "real"}};

    private static final String[][] combinazioniStr = {{"string", "string", "string"},
            {"string", "integer", "string"},
            {"string", "real", "string"},
            {"string", "boolean", "string"},
            {"integer", "string", "string"},
            {"real", "string", "string"},
            {"boolean", "string", "string"}};

    private static final String[][] combinazioniNot = {{"boolean", "boolean"}};
    private static final String[][] compatibilità = {{"integer", "integer"},
            {"real", "real"},
            {"string", "string"},
            {"boolean", "boolean"}};

    @Override
    public Object visit(BodyOp bodyOp) throws Exception {

        //controllo sulle variabili
        if (bodyOp.getVars() != null) {
            for (DeclOp decls : bodyOp.getVars()) {
                decls.accept(this);
            }
        }

        //controllo sugli statments
        if (bodyOp.getStatOps() != null) {
            for (StatOp stats : bodyOp.getStatOps()) {
                stats.accept(this);
            }
        }

        return null;
    }

    @Override
    public Object visit(ConstOp constOp) throws Exception {

        if (constOp.getTypeConst().equals("boolean_const")) {
            return "boolean";
        }
        if (constOp.getTypeConst().equals("integer_const")) {
            return "integer";
        }
        if (constOp.getTypeConst().equals("real_const")) {
            return "real";
        }
        if (constOp.getTypeConst().equals("string_const")) {
            return "string";
        }

        return null;
    }

    @Override
    public Object visit(DeclOp declOp) throws Exception {

        //controllo se il numero degli id e pari al numero delle costanti
        if (declOp.getConsts() != null && (declOp.getIds().size() != declOp.getConsts().size())) {
            throw new RuntimeException("Il numero degli id non è uguale al numero delle costanti in " + currentScope.getScope() + "!");
        }

        return null;
    }

    @Override
    public Object visit(ElifOp elifOp) throws Exception {

        String type = (String) elifOp.getExpr().accept(this);
        if (!type.equals("boolean")) {
            throw new RuntimeException("Condizione non valida. Tipo boolean non trovato!");
        }

        //Entro nello scope
        currentScope = elifOp.getSymbolTable();
        //Ottengo il tipo del body
        elifOp.getBody().accept(this);
        //Esco dallo scope
        currentScope = elifOp.getSymbolTable().getFather();

        return null;
    }

    @Override
    public Object visit(ExprOp exprOp) throws Exception {

        if (exprOp instanceof ConstOp || exprOp instanceof UnaryOp || exprOp instanceof Identifier) {
            return exprOp.accept(this);
        }

        if (exprOp instanceof FunCallOp) {
            return (exprOp).accept(this);
        }

        //casi binario Expr2
        exprOp.getExpr1().setTypeOp((String) exprOp.getExpr1().accept(this)); //mette il tipo che trova in expr1
        exprOp.getExpr2().setTypeOp((String) exprOp.getExpr2().accept(this)); //mette il tipo che trova in expr2

        String operationType = exprOp.getTypeOp(); //prendo il tipo di operazione

        //System.out.println("TV - Operation type: " + operationType);

        //controllo sulle operazioni +,-,*,/
        if (operationType.equals("PlusOp") || operationType.equals("MinusOp") || operationType.equals("TimesOp") || operationType.equals("DivOp")) {
            for (int i = 0; i < combinazioniOperatori.length; i++) {
                if (exprOp.getExpr1().getTypeOp().equals(combinazioniOperatori[i][0]) && exprOp.getExpr2().getTypeOp().equals(combinazioniOperatori[i][1])) {
                    exprOp.setTypeOp(combinazioniOperatori[i][2]);
                    return exprOp.getTypeOp();
                }
            }
            //controllo operazioni su stringhe
            for (int i = 0; i < combinazioniStr.length; i++) {
                if (exprOp.getExpr1().getTypeOp().equals(combinazioniStr[i][0]) && exprOp.getExpr2().getTypeOp().equals(combinazioniStr[i][1])) {
                    exprOp.setTypeOp(combinazioniStr[i][2]);
                    return exprOp.getTypeOp();
                }
            }
            throw new RuntimeException("Errore nell'operazione! (+,-,*,/)");
        }
        //controllo sulle operazioni di AND e di OR
        if (operationType.equals("AndOp") || operationType.equals("OrOp")) {
            if (exprOp.getExpr1().getTypeOp().equals(combinazioniBoolean[0]) && exprOp.getExpr2().getTypeOp().equals(combinazioniBoolean[1])) {
                exprOp.setTypeOp(combinazioniBoolean[2]);
                return exprOp.getTypeOp();
            }
            throw new RuntimeException("Errore nell'operazione! (AND e OR)");
        }
        //controllo sulle operazioni <,<=,>,>=,=,<>
        if (operationType.equals("GTOp") || operationType.equals("GEOp") || operationType.equals("LTOp") || operationType.equals("LEOp") ||
                operationType.equals("EQOp") || operationType.equals("NEOp")) {
            for (int i = 0; i < combinazioniRelop.length; i++) {
                if (exprOp.getExpr1().getTypeOp().equals(combinazioniRelop[i][0]) && exprOp.getExpr2().getTypeOp().equals(combinazioniRelop[i][1])) {
                    exprOp.setTypeOp(combinazioniRelop[i][2]);
                    return exprOp.getTypeOp();
                }
            }
            throw new RuntimeException("Errore nell'operazione! (<,<=,>,>=,=,<>) in " + currentScope.getScope());
        }

        return exprOp.getTypeOp();
        //return null;
    }

    @Override
    public Object visit(FunCallOp funCallOp) throws Exception {

        //prendo la dichiarazione della funzione dal type env
        RowTable res = currentScope.lookup(funCallOp.getId().getLessema());

        if (res == null) {
            throw new RuntimeException("La funzione chiamata in " + currentScope.getScope() + " non esiste!");
        } else {
            TypeField.TypeFieldFunction decl = (TypeField.TypeFieldFunction) res.getType();
            ArrayList<String> funparams = decl.getInputParam();

            int numPar = 0;

            //controllo num parametri
            if (funCallOp.getExprOps() != null) {
                numPar = funCallOp.getExprOps().size();
            }
            if (funparams.size() != numPar) {
                throw new RuntimeException("Il numero di parametri non coincide in " + currentScope.getScope() + "!");
            } else {
                if (numPar >= 1) {
                    //for(int k=0; k<funCallOp.getExprOps().size(); k++){
                        //System.out.println(funCallOp.getExprOps().get(k));
                        //funCallOp.getExprOps().get(k).accept(this);
                    // }
                    return funCallOp.getExprOps().get(0).accept(this);
                }

                //controllo se i tipi dei parametri coincidono
                for (int i = 0; i < numPar; i++) {
                    String type = (String) funCallOp.getExprOps().get(i).accept(this);
                    //System.out.println(type);
                    if (!type.equals(funparams.get(i))) {
                        throw new RuntimeException("I tipi dei parametri nella funzione chiamata non coincidono in " + currentScope.getFather());
                    }
                }

            }
        }

        return funCallOp.getTypeOp();
    }

    @Override
    public Object visit(FuncNodeOp funcNodeOp) throws Exception {

        currentScope = funcNodeOp.getSymbolTable();

        //aggiungo tipo di ritorno della funzione della lista
        for (TypeOp types : funcNodeOp.getTypes()) {
            returnTypes.addAll(Collections.singleton(types.getTypeVar()));
        }

        checkReturnForFunction(funcNodeOp.getBody(), currentScope); //controllo return

        //checkWriteandRead(funcNodeOp.getBody(), currentScope); //controllo read & write

        //riempio la lista con i parametri della funzione
        if (funcNodeOp.getFuncParams() != null) {
            for (FuncParamsOp f : funcNodeOp.getFuncParams()) {
                paramsFunc.add(f.getId().toString());
            }
        }

        funcNodeOp.getBody().accept(this); //si va a fare i controlli nel body

        //controllo se c'è almeno "return" nel corpo della funzione (dopo l'accept -> deve vedere tutto il body prima)
        if (!returnBodyFunc) {
            throw new RuntimeException("Ci deve essere almeno un return nel corpo della funzione!");
        }


        paramsFunc.clear();
        returnTypes.clear(); //pulisco l'array dei tipi in output per la prossima funzione
        returnBodyFunc = false; //lo setto a false per il controllo di almeno un return della funzione

        return null;
    }

    @Override
    public Object visit(FuncParamsOp funcParamsOp) throws Exception {
        return null;
    }

    @Override
    public Object visit(Identifier identifier) throws Exception {

        RowTable id = currentScope.lookup(identifier.getLessema());

        SymbolTable flagscope = currentScope;
        while (flagscope != null) {
            if (id != null) {
                String type = id.getType().toString();
                return type;
            }
            flagscope = flagscope.getFather();
        }

        throw new RuntimeException("Identificatore non dichiarato!" + identifier.getLessema());
    }

    @Override
    public Object visit(IfStatOp ifStatOp) throws Exception {

        String type = (String) ifStatOp.getExpr().accept(this);
        if (!type.equals("boolean")) {
            throw new RuntimeException("Condizione non valida. Tipo boolean non trovato!");
        }

        //Entro nello scope
        currentScope = ifStatOp.getSymbolTableIfBody();
        //Ottengo il tipo del body
        ifStatOp.getBody().accept(this);
        //Esco dallo scope
        //currentScope = ifStatOp.getSymbolTableIfBody();

        if (ifStatOp.getElifs() != null) {
            for (ElifOp elifs : ifStatOp.getElifs()) {
                elifs.accept(this);
            }
        }

        if (ifStatOp.getElsebody() != null) {
            //Entro nello scope
            currentScope = ifStatOp.getSymbolTableElsebody();
            //Ottengo il tipo del body
            ifStatOp.getElsebody().accept(this);
            //Esco dallo scope
            //currentScope = ifStatOp.getSymbolTableElsebody();
        }

        //controllo se nel corpo degli IF di una procedura ci sia "return"
        SymbolTable flagscope = currentScope;
        checkReturnForProcedure(ifStatOp.getBody(), flagscope);
        checkReturnForProcedure(ifStatOp.getElsebody(), flagscope);
        for (ElifOp s : ifStatOp.getElifs()) {
            checkReturnForProcedure(s.getBody(), flagscope);
        }

        //controllo se nel corpo degli IF di una funzione ci sia "return" e controllo tipo
        SymbolTable flagscope1 = currentScope;
        checkReturnForFunction(ifStatOp.getBody(), flagscope1);
        checkReturnForFunction(ifStatOp.getElsebody(), flagscope1);
        for (ElifOp s : ifStatOp.getElifs()) {
            checkReturnForFunction(s.getBody(), flagscope1);
        }

        return null;
    }

    @Override
    public Object visit(IterOp iterOp) throws Exception {

        //controllo sulle variabili
        if (iterOp.getVars() != null) {
            for (DeclOp decls : iterOp.getVars()) {
                decls.accept(this);
            }
        }

        //controllo sulle funzioni
        if (iterOp.getListfuncs() != null) {
            for (FuncNodeOp funcs : iterOp.getListfuncs()) {
                funcs.accept(this);
            }
        }

        //controllo sulle procedure
        if (iterOp.getListprocs() != null) {
            for (ProcNodeOp procs : iterOp.getListprocs()) {
                procs.accept(this);
            }
        }

        return null;
    }

    @Override
    public Object visit(ProcCallOp procCallOp) throws Exception {

        ArrayList<String> typecall = new ArrayList<>();
        ArrayList<String> refs = new ArrayList<>(); //Array per prendere i riferimenti (@) nella posizioni giuste

        //prendo la dichiarazione della funzione dal type env
        RowTable res = currentScope.lookup(procCallOp.getId().getLessema());

        if (res == null) {
            throw new RuntimeException("La procedura chiamata in " + currentScope.getScope() + " non esiste!");
        } else {

            ArrayList<String> procparams;

            //per i casi in cui venga chiamata una funzione in una procedura
            if (res.getType() instanceof TypeField.TypeFieldFunction typeFieldFunction)
                procparams = typeFieldFunction.getInputParam();
            else {
                TypeField.TypeFieldProcedure decl = (TypeField.TypeFieldProcedure) res.getType();
                procparams = decl.getInputParam(); //se lo prende dalla lista in typefiel --> per questo ho l'out
            }

            int numPar = 0;

            //controllo sul numero dei parametri
            if (procCallOp.getProcExprOps() != null) {
                numPar = procCallOp.getProcExprOps().size();
            }
            if (procparams.size() != numPar) {
                throw new RuntimeException("Nella chiamata a procedura in " + currentScope.getScope() + " il numero di parametri non coincide");
            } else {
                for (int i = 0; i < numPar; i++) {

                    String type = (String) procCallOp.getProcExprOps().get(i).accept(this); //per il controllo dei tipi della chiamata

                    if (!procparams.get(i).contains("out")) {
                        typecall.add(type); //out non presente
                    } else {
                        typecall.add("out " + type); //out presente
                    }
                    if (!typecall.get(i).equals(procparams.get(i))) {
                        throw new RuntimeException("I tipi dei parametri nella chiamata di funzione non coincide in " + currentScope.getScope());
                    }

                    //controllo se i ref sono posizionati in modo corretto nella chiamata a procedura
                    if (procCallOp.getProcExprOps().get(i) instanceof Identifier ids) {
                        refs.add(ids.getAttributo());
                    } else {
                        break;
                    }

                    //System.out.println("type: "+typecall.get(i) + " refs: "+refs.get(i));
                    if (typecall.get(i).contains("out") && !("ref".equals(refs.get(i)))) {
                        throw new RuntimeException("Errore nei parametri per riferimento nella chiamata di procedura in " + currentScope.getScope());
                    } else if (!typecall.get(i).contains("out") && ("ref".equals(refs.get(i)))) {
                        throw new RuntimeException("Alcuni parametri della chiamata a procedura sono stati passati erroneamente per riferimento in " + currentScope.getScope());
                    }

                }
                //System.out.println("tipi chiamata: "+typecall+" tipi paramtri:"+procparams +"scope: "+currentScope.getScope() );
            }
        }

        return null;
    }

    @Override
    public Object visit(ProcNodeOp procNodeOp) throws Exception {

        currentScope = procNodeOp.getSymbolTable();

        //controllo se c'è "return" nel corpo della procedura
        checkReturnForProcedure(procNodeOp.getBody(), currentScope);

        //controllo che nel main non ci siano parametri
        RowTable res = currentScope.lookup(procNodeOp.getId().getLessema());
        TypeField.TypeFieldProcedure decl = (TypeField.TypeFieldProcedure) res.getType();


        if(res.getSymbol().equals("main")){
            if(!decl.getInputParam().isEmpty()){
                throw new RuntimeException("Il main non può contenere parametri!");
            }
        }

        procNodeOp.getBody().accept(this);

        return null;
    }

    @Override
    public Object visit(ProcParamsOp procParamsOp) throws Exception {
        return null;
    }

    @Override
    public Object visit(ProgramOp programOp) throws Exception {

        currentScope = programOp.getSymbolTable();
        ArrayList<String> symbols = new ArrayList<>();
        String rt_main = null;

        //controllo se esiste una procedura chiamata main
        for (RowTable rt : currentScope.getListRow()) {
            String sym = rt.getSymbol();
            symbols.add(sym);

            if (sym.equals("main")) {
                rt_main = rt.getKind().toString();
            }
        }

        if (!symbols.contains("main")) {
            throw new RuntimeException("Procedura main non dichiarata");
        } else if (symbols.contains("main") && rt_main.equals("Func")) {
            throw new RuntimeException("Il main può essere solo una procedura!");
        }

        programOp.getIter().accept(this);

        return null;
    }

    @Override
    public Object visit(StatOp statOp) throws Exception {

        if (statOp.getLessema() != null) {
            if (statOp.getLessema().equals("return")) {
                if (statOp.getExprOps() != null) {
                    statOp.getExprOps().forEach(exprOp -> {
                        try {
                            exprOp.accept(this);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                return null;
            }

            if (statOp.getLessema().equals("write") || statOp.getLessema().equals("writereturn")) {
                if (statOp.getExprOps() != null) {
                    for (ExprOp e : statOp.getExprOps()) {
                        e.accept(this);
                    }
                }
            }

            if (statOp.getLessema().equals("read")) {
                if (!statOp.getExprOps().isEmpty()) {
                    for (int i = 0; i < statOp.getExprOps().size(); i++) {
                        //se l ultimo elemento non è un id lancio eccezione
                        if (((i == statOp.getExprOps().size() - 1) && !(statOp.getExprOps().get(i) instanceof Identifier))) {
                            throw new RuntimeException("L'operazione di input deve contenere necessariamente un'identificatore in " + currentScope.getScope() + "!");
                        }
                    }
                } else {
                    throw new RuntimeException("L'operazione di input deve contenere necessariamente un'identificatore in " + currentScope.getScope() + "!");
                }
            }

            if (statOp.getLessema().equals("assign")){
                ArrayList<String> exprstypesx = new ArrayList<>(); //lista per prendere i tipi delle variabili a sx della funzione

                //controllo sull'riassegnamento dei parametri delle funzioni
                int i = 0;
                for (Identifier ids : statOp.getIds()) {
                    if (!paramsFunc.isEmpty()) {
                        if (ids.getLessema().equals(paramsFunc.get(i))) {
                            throw new Exception("I parametri delle funzione " + currentScope.getScope() + " sono immutabili!");
                        }
                    }
                    i++;
                }

                //controllo sull'assegnazione su chiamata a funzione
                if (statOp.getExprOps() != null) {
                    for (ExprOp e : statOp.getExprOps()) {
                        e.accept(this);
                        if (e instanceof FunCallOp) {
                            for (Identifier ids : statOp.getIds()) {
                                String types = (String) ids.accept(this);
                                exprstypesx.addAll(Collections.singleton(types)); //aggiungo nell'arraylist tutti i tipi delle expr a sinistra della chiamata a funzione
                            }

                            RowTable r = currentScope.lookup(((FunCallOp) e).getId().getLessema());
                            TypeField.TypeFieldFunction funparamsout = (TypeField.TypeFieldFunction) r.getType(); //prendo i tipi dei paramentri della chiamata a funzione

                            //System.out.println(exprstypesx + " " + funparamsout.getOutputParam());

                            if (exprstypesx.size() != funparamsout.getOutputParam().size()) {
                                throw new RuntimeException("Il numero di variabili usati per invocare la funzione non sono corretti in " + currentScope.getScope());
                            }
                            if (!exprstypesx.equals(funparamsout.getOutputParam())) {
                                throw new RuntimeException("I tipi delle variabili usati per invocare la funzione non sono corretti in " + currentScope.getScope());
                            }
                        }
                    }
                }
            }

            if (statOp instanceof IfStatOp) {
                statOp.accept(this);
            }
            if (statOp instanceof ElifOp) {
                statOp.accept(this);
            }
            if (statOp instanceof WhileOp) {
                statOp.accept(this);
            }

            if (statOp instanceof ProcCallOp) {
                statOp.accept(this);
            }
        }
        return null;
    }

    @Override
    public Object visit(TypeOp typeOp) throws Exception {
        return null;
    }

    @Override
    public Object visit(UnaryOp unaryOp) throws Exception {
        String typeE = (String) unaryOp.getExpr().accept(this);

        if (unaryOp.getType().equals("UminusOp")) {
            for (String[] comb : combinazioniMinus) {
                if (typeE.equals(comb[0])) {
                    unaryOp.setType(comb[1]);
                    return comb[1];
                }
            }
            throw new RuntimeException("Operazione UMINUS non valida!");
        }

        if (unaryOp.getType().equals("NotOp")) {
            for (String[] comb : combinazioniNot) {
                if (typeE.equals(comb[0])) {
                    unaryOp.setType(comb[1]);
                    return comb[1];
                }
            }
            throw new RuntimeException("Operazione NOT non valida!");
        }

        throw new RuntimeException("Operazione unaria non riconosciuta!");
    }

    @Override
    public Object visit(WhileOp whileOp) throws Exception {
        String type = (String) whileOp.getExpr().accept(this);

        if (!type.equals("boolean")) {
            throw new RuntimeException("Condizione non valida. Tipo boolean non trovato!");
        }

        currentScope = whileOp.getSymbolTable();

        whileOp.getBody().accept(this);

        currentScope = whileOp.getSymbolTable();

        //controllo se nel corpo del while di una procedura ci sia "return"
        SymbolTable flagscope = currentScope;
        checkReturnForProcedure(whileOp.getBody(), flagscope);

        //controllo se nel corpo del while di una funzione ci sia "return" e controllo i tipi
        SymbolTable flagscope1 = currentScope;
        checkReturnForFunction(whileOp.getBody(), flagscope1);

        return null;
    }

    public void checkReturn() {

        if (returnTypes.size() != returnExpr.size()) {
            throw new RuntimeException("Il numero dei parametri in output non combacia con quelli del return in" + currentScope.getScope());
        } else {
            for (int i = 0; i < returnTypes.size(); i++) {
                //System.out.println(returnTypes.get(i));
                if (!returnTypes.get(i).equals(returnExpr.get(i))) {
                    throw new RuntimeException("I tipi dei parametri in output non combaciano con quelli del return in " + currentScope.getScope());
                }

            }
        }

        //returnExpr.clear(); //pulisco l'array dei tipi di ritorno per il prossimo scope
    }

    private void checkReturnForFunction(BodyOp bodyOp, SymbolTable flagscope) throws Exception {

        boolean containsFuncCall = false;

        while (flagscope != null) {
            if (flagscope.getScope().contains("Func")) {
                if (bodyOp != null) {
                    for (StatOp stat : bodyOp.getStatOps()) {
                        if (stat.getLessema() != null && stat.getLessema().equals("return")) {

                            returnExpr.clear(); //pulisce array per controllo su tutti i return nel corpo della funzione e per i prossimi scope
                            returnBodyFunc = true; //lo setto a "true" perché almeno un return è stato trovato

                            for (ExprOp e : stat.getExprOps()) {

                                if (e instanceof FunCallOp funCallOp) {
                                    funCallOp.accept(this);
                                    containsFuncCall = true;
                                } else {
                                    returnExpr.add((String) e.accept(this));
                                }
                            }
                            if (!containsFuncCall) {
                                //System.out.println("return type: " + returnTypes + " return expr: " + returnExpr);
                                //containsFuncCall = false;
                                checkReturn(); //chiama la funzione per il controllo (a meno che dopo il return non ci sia una chiamata a funz)
                            }
                        }
                    }
                }
                break;
            } else
                flagscope = flagscope.getFather();
        }
    }

    private void checkReturnForProcedure(BodyOp bodyOp, SymbolTable flagscope) {
        while (flagscope != null) {
            if (flagscope.getScope().contains("Proc")) {
                if (bodyOp != null) {
                    for (StatOp stat : bodyOp.getStatOps()) {
                        if (stat.getLessema() != null && stat.getLessema().equals("return")) {
                            throw new RuntimeException("La procedura non può contenere return in " + flagscope.getScope() + "!");
                        }
                    }
                }
                break;
            } else
                flagscope = flagscope.getFather();
        }
    }

}
