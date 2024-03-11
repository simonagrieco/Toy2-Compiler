package nodi;

import visitors.Visitable;
import visitors.Visitor;

public class UnaryOp extends ExprOp implements Visitable {
    String type;
    ExprOp expr;

    public UnaryOp(String type, ExprOp expr) {
        super(type);
        this.type = type;
        this.expr = expr;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ExprOp getExpr() {
        return expr;
    }

    public void setExpr(ExprOp expr) {
        this.expr = expr;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}
}
