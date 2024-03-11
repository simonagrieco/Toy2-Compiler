package nodi;

import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class StatOp extends DefaultMutableTreeNode implements Visitable {
    String lessema;
    ArrayList<Identifier> ids;
    ArrayList<ExprOp> exprOps;

    public StatOp(String lessema, ArrayList<Identifier> ids, ArrayList<ExprOp> exprOps) {
        super(lessema);
        this.lessema = lessema;

        for (Identifier e: ids) {
            super.add(e);
        }
        this.ids = ids;

        for (ExprOp e: exprOps) {
            super.add(e);
        }
        this.exprOps = exprOps;
    }
    public StatOp(ArrayList<Identifier> ids, ArrayList<ExprOp> exprOps) {
        super("StatOp");
        for (Identifier e: ids) {
            super.add(e);
        }
        this.ids = ids;

        for (ExprOp e: exprOps) {
            super.add(e);
        }
        this.exprOps = exprOps;
    }

    public StatOp(String lessema, ArrayList<ExprOp> exprOps) {
        super(lessema);

        this.lessema = lessema;

        for (ExprOp e: exprOps) {
            super.add(e);
        }
        this.exprOps = exprOps;

    }
    public StatOp(String lessema){
        super(lessema);
        this.lessema = lessema;
    }

    public String getLessema() {
        return lessema;
    }

    public void setLessema(String lessema) {
        this.lessema = lessema;
    }

    public ArrayList<Identifier> getIds() {
        return ids;
    }

    public void setIds(ArrayList<Identifier> ids) {
        this.ids = ids;
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
