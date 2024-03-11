package nodi;

import symbolTable.SymbolTable;
import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class ProgramOp extends DefaultMutableTreeNode implements Visitable {

    IterOp iter;
    private SymbolTable symbolTable;

    public ProgramOp(IterOp iterproc, ArrayList<ProcNodeOp> procnode, IterOp iter){
        super("ProgramOp");

        IterOp resultIter = IterOp.mergeItersAndProc(iterproc, procnode, iter);
        this.iter = resultIter;
        super.add(resultIter);
    }

    public IterOp getIter() {
        return iter;
    }

    public void setIter(IterOp iter) {
        this.iter = iter;
    }

    public SymbolTable getSymbolTable(){ return this.symbolTable;}

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}

}
