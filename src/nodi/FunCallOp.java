package nodi;

import visitors.Visitable;
import visitors.Visitor;

import java.util.ArrayList;

public class FunCallOp extends ExprOp implements Visitable {
    Identifier id;
    ArrayList<ExprOp> exprOps;

    public FunCallOp(Identifier id, ArrayList<ExprOp> exprOps) {
        super("FunCallOp");
        this.id = id;
        for (ExprOp e: exprOps) {
            super.add(e);
        }
        this.exprOps = exprOps;
       // super.add(new DefaultMutableTreeNode(id));
    }

    public FunCallOp(Identifier id) {
        super("FunCallOp");
        this.id = id;
        super.add(id);
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }


    public ArrayList<ExprOp> getExprOps() {
        return exprOps;
    }

    public void setExprOps(ArrayList<ExprOp> exprOps) {
        this.exprOps = exprOps;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}
}
