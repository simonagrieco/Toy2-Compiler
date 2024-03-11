package nodi;

import symbolTable.*;
import visitors.Visitable;
import visitors.Visitor;

import java.util.ArrayList;

public class IfStatOp extends StatOp implements Visitable {
    ExprOp exprOp;
    BodyOp body;
    ArrayList<ElifOp> elifs;
    BodyOp elsebody;

    SymbolTable symbolTableIfBody;
    SymbolTable symbolTableElsebody;

    public IfStatOp(ExprOp exprOp, BodyOp body) {
        super("IfStatOp");
        this.exprOp = exprOp;
        this.body = body;

        super.add(exprOp);
        super.add(body);
    }

    public IfStatOp(ExprOp exprOp, BodyOp body, ArrayList<ElifOp> elifs, BodyOp elsebody){
        super("IfStatOp");

        this.exprOp = exprOp;
        this.body = body;
        this.elifs = elifs;

        super.add(exprOp);
        super.add(body);

        for (ElifOp e: elifs) {
            super.add(e);
        }
        this.elsebody = elsebody;

        super.add(elsebody);
    }
    public IfStatOp(ExprOp exprOp, BodyOp body, ArrayList<ElifOp> elifs){
        super("IfStatOp");

        this.exprOp = exprOp;
        this.body = body;
        super.add(exprOp);
        super.add(body);

        for (ElifOp e: elifs) {
            super.add(e);
        }
        this.elifs = elifs;
    }

    public IfStatOp(ExprOp exprOp, BodyOp body, BodyOp elsebody){
        super("IfStatOp");

        this.exprOp = exprOp;
        this.body = body;
        this.elsebody = elsebody;

        super.add(exprOp);
        super.add(body);
        super.add(elsebody);
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

    public ArrayList<ElifOp> getElifs() {
        return elifs;
    }

    public void setElifs(ArrayList<ElifOp> elifs) {
        this.elifs = elifs;
    }

    public BodyOp getElsebody() {
        return elsebody;
    }

    public void setElsebody(BodyOp elsebody) {
        this.elsebody = elsebody;
    }

    public SymbolTable getSymbolTableIfBody() {return this.symbolTableIfBody;}

    public void setSymbolTableIfBody(SymbolTable symbolTable) {this.symbolTableIfBody=symbolTable;}

    public SymbolTable getSymbolTableElsebody() {return this.symbolTableElsebody;}

    public void setSymbolTableElsebody(SymbolTable symbolTable) {this.symbolTableElsebody=symbolTable;}

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}
}
