package nodi;

import symbolTable.SymbolTable;
import visitors.Visitable;
import visitors.Visitor;

public class ElifOp extends StatOp implements Visitable {
    ExprOp exprOp;
    BodyOp body;
    SymbolTable symbolTable;

    public ElifOp(ExprOp exprOp, BodyOp body) {
        super("ElifOp");
        this.exprOp = exprOp;
        this.body = body;

        super.add(exprOp);
        super.add(body);
    }

    public ExprOp getExpr() {
        return exprOp;
    }

    public void setExpr(ExprOp exprOp) {
        this.exprOp = exprOp;
    }

    public BodyOp getBody() {
        return body;
    }

    public void setBody(BodyOp body) {
        this.body = body;
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

    public String toString() {return super.toString();}

    public ExprOp getExprOp() {
        return exprOp;
    }

    public void setExprOp(ExprOp exprOp) {
        this.exprOp = exprOp;
    }
}
