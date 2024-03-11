package visitors;

import nodi.*;
import symbolTable.*;

import java.util.ArrayList;
import java.util.Collections;

public class ScopeVisitor implements Visitor {

    private SymbolTable father = null;

    //genero scope del body
    @Override
    public Object visit(BodyOp bodyOp) throws Exception {

        if (bodyOp.getVars() != null) {
            ArrayList<RowTable> varList;
            for (DeclOp vars : bodyOp.getVars()) {
                varList = (ArrayList<RowTable>) vars.accept(this);
                if (varList != null) {
                    for (RowTable row : varList) {
                        father.addRow(row);
                    }
                }
            }
        }

        Collections.reverse(bodyOp.getStatOps()); //risolve problema degli statments al contrario

        if (bodyOp.getStatOps() != null) {
            for (StatOp stats : bodyOp.getStatOps()) {
                stats.accept(this);
            }
        }
        return null;
    }

    @Override
    public Object visit(ConstOp constOp) throws Exception {
        String type = constOp.getTypeConst();
        if (type.equals("real_const"))
            return "real";
        if (type.equals("integer_const"))
            return "integer";
        if (type.equals("string_const"))
            return "string";
        if (type.equals("boolean_const"))
            return "boolean";

        return null;
    }


    @Override
    public Object visit(DeclOp declOp) throws Exception {

        //caso -> a:integer;
        if (declOp.getType() != null) {
            for (Identifier ids : declOp.getIds()) {
                father.addRow(new RowTable(ids.getLessema(), "var", new TypeField.TypeFieldVar(declOp.getType().getTypeVar()), ""));
            }
        } else {
            //caso -> a ^= 1;
            if (declOp.getConsts() != null) {
                for (int i = 0; i < declOp.getIds().size() && i < declOp.getConsts().size(); i++) {

                    Identifier id = declOp.getIds().get(i);
                    ConstOp constant = declOp.getConsts().get(i);

                    TypeOp tipoCostante = new TypeOp((String) constant.accept(this));
                    String typeconst = tipoCostante.getTypeVar(); //prendo il nome del tipo effettivo


                    //variabili globali
                    father.addRow(new RowTable(id.getLessema(), "var", new TypeField.TypeFieldVar(typeconst), ""));
                }
            }
        }

        return null;
    }

    @Override
    public Object visit(ElifOp elifOp) throws Exception {
        elifOp.setSymbolTable(new SymbolTable());
        SymbolTable symbolTableElifBody = elifOp.getSymbolTable();
        symbolTableElifBody.setScope("Else-If");
        symbolTableElifBody.setFather(father);

        if (elifOp.getBody() != null) {
            father = elifOp.getSymbolTable();
            elifOp.getBody().accept(this);
        }
        elifOp.getExpr().accept(this);
        return null;
    }

    @Override
    public Object visit(ExprOp exprOp) throws Exception {
        exprOp.getExpr1().accept(this);
        exprOp.getExpr2().accept(this);
        return null;
    }

    @Override
    public Object visit(FunCallOp funCallOp) throws Exception {
        if (funCallOp.getExprOps() != null) {
            for (ExprOp e : funCallOp.getExprOps()) {
                e.accept(this);
            }
        }
        return null;
    }

    //si genera lo scope della funzione, aggiungendo i parametri
    @Override
    public Object visit(FuncNodeOp funcNodeOp) throws Exception {

        funcNodeOp.setSymbolTable(new SymbolTable());
        SymbolTable symbolTableFunc = funcNodeOp.getSymbolTable();
        symbolTableFunc.setScope("Func(" + funcNodeOp.getId().getLessema() + ")");
        symbolTableFunc.setFather(father);


        // Aggiungo i parametri allo scope, se sono presenti
        if (funcNodeOp.getFuncParams() != null) {
            for (FuncParamsOp param : funcNodeOp.getFuncParams()) {
                RowTable paramRow = (RowTable) param.accept(this);
                symbolTableFunc.addRow(paramRow);
            }
        }

        if (funcNodeOp.getBody() != null) {
            father = funcNodeOp.getSymbolTable();
            funcNodeOp.getBody().accept(this);
        }

        //father=symbolTableFunc;
        System.out.println(father);
        father = funcNodeOp.getSymbolTable().getFather(); //esco fuori dallo scope

        return null;
    }

    @Override
    public Object visit(FuncParamsOp funcParamsOp) throws Exception {
        if (funcParamsOp != null) {
            return new RowTable(funcParamsOp.getId().getLessema(), "var", new TypeField.TypeFieldVar(funcParamsOp.getTypeOp().getTypeVar()), "immutable");
        }
        return null;
    }


    @Override
    public Object visit(Identifier identifier) throws Exception {
        if (father.lookup(identifier.getLessema()) == null) {
            throw new RuntimeException("Id non dichiarato: " + identifier.getLessema());
        }
        return null;
    }


    //genero scope per l'if
    @Override
    public Object visit(IfStatOp ifStatOp) throws Exception {
        ifStatOp.setSymbolTableIfBody(new SymbolTable());
        SymbolTable symbolTableBody = ifStatOp.getSymbolTableIfBody();
        symbolTableBody.setScope("If");
        symbolTableBody.setFather(father);

        ifStatOp.setSymbolTableElsebody(new SymbolTable());
        SymbolTable symbolTableElsebody = ifStatOp.getSymbolTableElsebody();
        symbolTableElsebody.setScope("ElseBody");
        symbolTableElsebody.setFather(father);

        if (ifStatOp.getBody() != null) {
            father = ifStatOp.getSymbolTableIfBody();
            ifStatOp.getBody().accept(this);
        }

        if (ifStatOp.getElsebody() != null) {
            father = ifStatOp.getSymbolTableElsebody();
            ifStatOp.getElsebody().accept(this);
        }

        if (ifStatOp.getElifs() != null) {
            for (ElifOp elifOp : ifStatOp.getElifs()) {
                elifOp.accept(this);
            }
        }

        //System.out.println(father);
        father = ifStatOp.getSymbolTableIfBody().getFather();
        return null;
    }

    @Override
    public Object visit(IterOp iterOp) throws Exception {

        if (iterOp.getVars() != null) {
            for (DeclOp var : iterOp.getVars()) {
                var.accept(this);
            }
        }

        if (iterOp.getListfuncs() != null) {

            //System.out.println(iterOp.getListfuncs().size());

            for (FuncNodeOp funs : iterOp.getListfuncs()) {

                //TypeField.TypeFieldFunction typeField = new TypeField.TypeFieldFunction();

                ArrayList<String> typesInput = new ArrayList<>();
                for(FuncParamsOp t: funs.getFuncParams()){
                    typesInput.add(t.getTypeOp().getTypeVar());
                }

                ArrayList<String> typesOutput = new ArrayList<>();
                for(TypeOp t: funs.getTypes()){
                    typesOutput.add(t.getTypeVar());
                }

                //firma globale
                RowTable rt = new RowTable(funs.getId().getLessema(), "Func", new TypeField.TypeFieldFunction(typesInput, typesOutput), "");
                father.addRow(rt);

                funs.accept(this);
            }
        }


        if (iterOp.getListprocs() != null) {
            for (ProcNodeOp proc : iterOp.getListprocs()) {

                ArrayList<String> types = new ArrayList<>();
                for(ProcParamsOp t: proc.getProcParamOps()){
                    if(t.getId().getAttributo()!=null && t.getId().getAttributo().equals("out")){
                        types.add(t.getId().getAttributo() + " " + t.getTypeOp().getTypeVar());
                    } else {
                        types.add(t.getTypeOp().getTypeVar());
                    }
                }

                //firma globale
                RowTable rt = new RowTable(proc.getId().getLessema(), "Proc", new TypeField.TypeFieldProcedure(types), "");
                father.addRow(rt);

                proc.accept(this);
            }
        }

        return null;

    }

    @Override
    public Object visit(ProcCallOp procCallOp) throws Exception {
        if (procCallOp.getProcExprOps() != null) {
            for (ExprOp e : procCallOp.getProcExprOps()) {
                e.accept(this);
            }
        }
        return null;
    }

    //si genera lo scope della procedura, aggiungendo i parametri
    @Override
    public Object visit(ProcNodeOp procNodeOp) throws Exception {
        procNodeOp.setSymbolTable(new SymbolTable());
        SymbolTable symbolTableProc = procNodeOp.getSymbolTable();
        symbolTableProc.setScope("Proc(" + procNodeOp.getId().getLessema() + ")");
        symbolTableProc.setFather(father);

        //aggiungo i parametri allo scope, se sono presenti
        if (procNodeOp.getProcParamOps() != null) {
            for (ProcParamsOp param : procNodeOp.getProcParamOps()) {
                RowTable paramRow = (RowTable) param.accept(this);
                symbolTableProc.addRow(paramRow);
            }
        }

        if (procNodeOp.getBody() != null) {
            father = procNodeOp.getSymbolTable();
            procNodeOp.getBody().accept(this);
        }


        System.out.println(father);
        father = procNodeOp.getSymbolTable().getFather(); //esco fuori dallo scope

        return null;
    }

    @Override
    public Object visit(ProcParamsOp procParamsOp) throws Exception {
        if (procParamsOp != null) {
            if(procParamsOp.getId().getAttributo() != null && procParamsOp.getId().getAttributo().contains("out")) {
                procParamsOp.getId().setOut(true);
                return new RowTable(procParamsOp.getId().getLessema(), "var", new TypeField.TypeFieldVar(procParamsOp.getTypeOp().getTypeVar()), "ref");
            }
            else {
                return new RowTable(procParamsOp.getId().getLessema(), "var", new TypeField.TypeFieldVar(procParamsOp.getTypeOp().getTypeVar()), "");
            }

            //aggiunge decls nello scope della
            //return new RowTable(procParamsOp.getId().getLessema(), "var", new TypeField.TypeFieldVar(procParamsOp.getTypeOp().getTypeVar()), "");

        }
        return null;
    }

    @Override
    public Object visit(ProgramOp programOp) throws Exception {

        programOp.setSymbolTable(new SymbolTable());
        SymbolTable symbolTable = programOp.getSymbolTable();
        symbolTable.setScope("Root");
        symbolTable.setFather(null); //programop non ha un padre :(

        father = symbolTable;

        programOp.getIter().accept(this);

        System.out.println(father);
        return null;
    }

    @Override
    public Object visit(StatOp stat) throws Exception {

        if (stat instanceof WhileOp) {
            //si genera lo scope del While
            var s = (WhileOp) stat;
            s.accept(this);
        } else if (stat instanceof IfStatOp) {
            //si genera lo scope dell'If
            var s = (IfStatOp) stat;
            s.accept(this);
        } else if (stat instanceof ElifOp) {
            //si genera lo scope dell'Elif
            var s = (ElifOp) stat;
            s.accept(this);
        } else if (stat instanceof ProcCallOp) {
            //si genera lo scope della Procedura
            var s = (ProcCallOp) stat;
            s.accept(this);
        }

        if (stat.getIds() != null) {
            for (Identifier ids : stat.getIds()) {
                ids.accept(this);
            }
        }
        if(stat.getExprOps() != null) {
            for (ExprOp exprs : stat.getExprOps()) {
                exprs.accept(this);
            }
        }
        return null;
    }

    @Override
    public Object visit(TypeOp typeOp) throws Exception {
        String type = typeOp.getTypeVar();
        if (type.equals("real"))
            return "real";
        if (type.equals("integer"))
            return "integer";
        if (type.equals("string"))
            return "string";
        if (type.equals("boolean"))
            return "boolean";
        return null;
    }

    @Override
    public Object visit(UnaryOp unaryOp) throws Exception {

        return null;
    }

    //genero lo scope per il while
    @Override
    public Object visit(WhileOp whileOp) throws Exception {
        whileOp.setSymbolTable(new SymbolTable());
        SymbolTable symbolTable = whileOp.getSymbolTable();
        symbolTable.setScope("While");
        symbolTable.setFather(father);

        if (whileOp.getBody() != null) {
            father = whileOp.getSymbolTable();
            whileOp.getBody().accept(this);
        }

        //System.out.println(father);
        father = whileOp.getSymbolTable().getFather(); //esco fuori dallo scope
        return null;
    }
}
