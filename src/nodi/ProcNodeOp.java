package nodi;

import symbolTable.SymbolTable;
import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class ProcNodeOp extends DefaultMutableTreeNode implements Visitable {
    Identifier id;
    ArrayList<ProcParamsOp> procParamOps;
    BodyOp body;

    SymbolTable symbolTable;

    public ProcNodeOp(Identifier id, ArrayList<ProcParamsOp> procParamOps, BodyOp body) {
        super("ProcNodeOp");

        this.id = id;
        super.add(id);

        this.procParamOps = procParamOps;
        for (ProcParamsOp e: procParamOps) {
            super.add(e);
        }

        this.body = body;
        super.add(body);
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public BodyOp getBody() {
        return body;
    }

    public void setBody(BodyOp body) {
        this.body = body;
    }

    public ArrayList<ProcParamsOp> getProcParamOps() {
        return procParamOps;
    }

    public void setProcParamOps(ArrayList<ProcParamsOp> procParamOps) {
        this.procParamOps = procParamOps;
    }

    public String toString() {return super.toString();}

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
