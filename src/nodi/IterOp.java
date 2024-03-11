package nodi;

import symbolTable.SymbolTable;
import visitors.Visitable;
import visitors.Visitor;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class IterOp extends DefaultMutableTreeNode implements Visitable {
    ArrayList<DeclOp> vars;
    ArrayList<FuncNodeOp> listfuncs;
    ArrayList<ProcNodeOp> listprocs;

    SymbolTable symbolTable;

    public IterOp(ArrayList<DeclOp> vars, ArrayList<FuncNodeOp> listfuncs, ArrayList<ProcNodeOp> listprocs){
        super("IterOp");

        for (DeclOp e: vars) {
            super.add(e);
        }
        this.vars= vars;

        for (FuncNodeOp e: listfuncs) {
            super.add(e);
        }
        this.listfuncs= listfuncs;

        for (ProcNodeOp e: listprocs) {
            super.add(e);
        }
        this.listprocs= listprocs;
    }

    public IterOp(ArrayList<DeclOp> vars, ArrayList<FuncNodeOp> listfuncs){
        super("IterOp");

        for (DeclOp e: vars) {
            super.add(e);
        }
        this.vars= vars;

        for (FuncNodeOp e: listfuncs) {
            super.add(e);
        }
        this.listfuncs= listfuncs;
    }

    public static IterOp mergeItersAndProc(IterOp iter, ArrayList<ProcNodeOp> procedure, IterOp iterNoProc) {
        ArrayList<DeclOp> resultDecls = new ArrayList<>();
        resultDecls.addAll(iter.vars);
        resultDecls.addAll(iterNoProc.vars);

        ArrayList<FuncNodeOp> resultFuncs = new ArrayList<>();
        resultFuncs.addAll(iter.listfuncs);
        resultFuncs.addAll(iterNoProc.listfuncs);
        //System.out.println(resultFuncs.size());


        ArrayList<ProcNodeOp> resultProcs = new ArrayList<>();
        if(iter.listprocs!= null && !iter.listprocs.isEmpty())
            resultProcs.addAll(iter.listprocs);

        if(iterNoProc.listprocs!= null && !iterNoProc.listprocs.isEmpty())
            resultProcs.addAll(iterNoProc.listprocs);

        if(procedure != null && !procedure.isEmpty())
            resultProcs.addAll(procedure);

        return new IterOp(resultDecls, resultFuncs, resultProcs);
    }

    public ArrayList<DeclOp> getVars() {
        return vars;
    }

    public void setVars(ArrayList<DeclOp> vars) {
        this.vars = vars;
    }

    public ArrayList<FuncNodeOp> getListfuncs() {
        return listfuncs;
    }

    public void setListfuncs(ArrayList<FuncNodeOp> listfuncs) {
        this.listfuncs = listfuncs;
    }

    public ArrayList<ProcNodeOp> getListprocs() {
        return listprocs;
    }

    public void setListprocs(ArrayList<ProcNodeOp> listprocs) {
        this.listprocs = listprocs;
    }


    public IterOp addVars(ArrayList<DeclOp> var) {
        if (vars == null) {
            vars = new ArrayList<>();
        }
        vars.addAll(var);
        for (DeclOp e : vars) {
            super.add(e);
        }
        return this;
    }

    public IterOp addFunction(ArrayList<FuncNodeOp> funcs) {
        if (listfuncs == null) {
            listfuncs = new ArrayList<>();
        }
        listfuncs.addAll(funcs);
        for (FuncNodeOp e : listfuncs) {
            super.add(e);
        }
        return this;
    }

    public IterOp addProcedure(ArrayList<ProcNodeOp> procs) {
        if (listprocs == null) {
            listprocs = new ArrayList<>();
        }
        listprocs.addAll(procs);
        for (ProcNodeOp e : listprocs) {
            super.add(e);
        }
        return this;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }
}
