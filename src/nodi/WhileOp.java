package nodi;

import visitors.Visitable;
import visitors.Visitor;
import symbolTable.*;

public class WhileOp extends StatOp implements Visitable {
    ExprOp exprOp;
    BodyOp body;
    SymbolTable symbolTable;

    public WhileOp(ExprOp exprOp, BodyOp body) {
        super("WhileOp");
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
        return this.symbolTable;
    }

    public void setExprOp(ExprOp exprOp) {
        this.exprOp = exprOp;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}


