package nodi;

import visitors.Visitable;
import visitors.Visitor;

import java.util.ArrayList;

public class ProcCallOp extends StatOp implements Visitable {
    Identifier id;
    ArrayList<ExprOp> procExprOps;

    public ProcCallOp(Identifier id, ArrayList<ExprOp> procExprOps) {
        super("ProcCallOp");
        this.id = id;
        for (ExprOp e: procExprOps) {
            super.add(e);
        }
        this.procExprOps = procExprOps;
    }

    public ProcCallOp(Identifier id) {
        super("ProcCallOp");
        this.id = id;
        super.add(id);
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public ArrayList<ExprOp> getProcExprOps() {
        return procExprOps;
    }

    public void setProcExprOps(ArrayList<ExprOp> procExprOps) {
        this.procExprOps = procExprOps;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}
}

