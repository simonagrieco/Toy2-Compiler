package nodi;

import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;

public class ConstOp extends ExprOp implements Visitable {

    private String lessema;
    private String typeConst;

    public ConstOp(String typeConst, String lessema){
        super("ConstOp");

        this.typeConst=typeConst;
        this.lessema=lessema;

        super.add(new DefaultMutableTreeNode(typeConst));
        super.add(new DefaultMutableTreeNode(lessema));

    }

    public ConstOp(String typeConst){
        super("ConstOp");

        super.add(new DefaultMutableTreeNode(typeConst));
        this.typeConst= typeConst;
    }

    public String getLessema() {
        return lessema;
    }

    public void setLessema(String lessema) {
        this.lessema = lessema;
    }

    public String getTypeConst() {
        return typeConst;
    }

    public void setTypeConst(String typeConst) {
        this.typeConst = typeConst;
    }


    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}
}
