package nodi;


import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;

public class ExprOp extends DefaultMutableTreeNode implements Visitable {

    String typeOp;
    ExprOp exprOp1;
    ExprOp exprOp2;

    public ExprOp(String typeOp, ExprOp exprOp1, ExprOp exprOp2) {
        super(typeOp);
        this.typeOp = typeOp;
        this.exprOp1 = exprOp1;
        this.exprOp2 = exprOp2;
        //super.add(new DefaultMutableTreeNode(typeOp));
        super.add(exprOp1);
        super.add(exprOp2);
    }

    public ExprOp(String typeOp) {
        super(typeOp);
        this.typeOp = typeOp;

        //super.add(new DefaultMutableTreeNode(typeOp));
    }

    public ExprOp() {

    }

    public String getTypeOp() {
        return typeOp;
    }

    public void setTypeOp(String typeOp) {
        this.typeOp = typeOp;
    }

    public ExprOp getExpr1() {
        return exprOp1;
    }

    public void setExpr1(ExprOp exprOp1) {
        this.exprOp1 = exprOp1;
    }

    public ExprOp getExpr2() {
        return exprOp2;
    }

    public void setExpr2(ExprOp exprOp2) {
        this.exprOp2 = exprOp2;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}

    public ExprOp getExprOp1() {
        return exprOp1;
    }

    public void setExprOp1(ExprOp exprOp1) {
        this.exprOp1 = exprOp1;
    }

    public ExprOp getExprOp2() {
        return exprOp2;
    }

    public void setExprOp2(ExprOp exprOp2) {
        this.exprOp2 = exprOp2;
    }
}