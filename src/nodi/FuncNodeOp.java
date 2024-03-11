package nodi;

import symbolTable.SymbolTable;
import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class FuncNodeOp extends DefaultMutableTreeNode implements Visitable {
    Identifier id;
    ArrayList<FuncParamsOp> funcParamOps; //parametri funzione
    ArrayList<TypeOp> typeOps; //tipi di ritorno
    BodyOp body;

    SymbolTable symbolTable;

    public FuncNodeOp(Identifier id, ArrayList<FuncParamsOp> funcParamOps, ArrayList<TypeOp> typeOps, BodyOp body) {
        super("FuncNodeOp");

        this.id = id;
        super.add(id);

        for (FuncParamsOp e: funcParamOps) {
            super.add(e);
        }
        this.funcParamOps = funcParamOps;

        for (TypeOp e: typeOps) {
            super.add(e);
        }
        this.typeOps = typeOps;

        this.body = body;
        super.add(body);
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public ArrayList<FuncParamsOp> getFuncParams() {
        return funcParamOps;
    }

    public void setFuncParams(ArrayList<FuncParamsOp> funcParamOps) {
        this.funcParamOps = funcParamOps;
    }

    public ArrayList<TypeOp> getTypes() {
        return typeOps;
    }

    public void setTypes(ArrayList<TypeOp> typeOps) {
        this.typeOps = typeOps;
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


    public void setFuncParamOps(ArrayList<FuncParamsOp> funcParamOps) {
        this.funcParamOps = funcParamOps;
    }

    public ArrayList<TypeOp> getTypeOps() {
        return typeOps;
    }

    public void setTypeOps(ArrayList<TypeOp> typeOps) {
        this.typeOps = typeOps;
    }
}
