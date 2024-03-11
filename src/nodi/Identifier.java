package nodi;


import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;

public class Identifier extends ExprOp implements Visitable {
    String attributo;
    String lessema;
    boolean isOut;

    public Identifier(String attributo, String lessema) {
        super(lessema);
        this.attributo = attributo;
        this.lessema = lessema;
        super.add(new DefaultMutableTreeNode(attributo));
    }

    public Identifier(String lessema) {
        super(lessema);
        this.lessema = lessema;
    }

    public String getAttributo() {
        return attributo;
    }

    public void setAttributo(String attributo) {
        this.attributo = attributo;
    }

    public String getLessema() {
        return lessema;
    }

    public void setLessema(String lessema) {
        this.lessema = lessema;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        isOut = out;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}


}
