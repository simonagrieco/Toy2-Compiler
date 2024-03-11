package nodi;

import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;

public class TypeOp extends DefaultMutableTreeNode implements Visitable {

    private String typeVar;

    public TypeOp(String typeVar) {
        super("TypeOp");
        super.add(new DefaultMutableTreeNode(typeVar));
        this.typeVar = typeVar;
    }

    public String getTypeVar() {
        return typeVar;
    }

    public void setTypeVar(String typeVar) {
        this.typeVar = typeVar;
    }


    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}
}
