package nodi;

import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class DeclOp extends DefaultMutableTreeNode implements Visitable {
    ArrayList<Identifier> ids;
    TypeOp type;
    ArrayList<ConstOp> consts;

    public DeclOp(ArrayList<Identifier> ids, TypeOp type) {
        super("DeclOp");
        for (Identifier e: ids) {
            super.add(e);
        }
        this.ids = ids;
        this.type = type;
        super.add(type);
    }

    public DeclOp(ArrayList<Identifier> ids, ArrayList<ConstOp> consts) {
        super("DeclOp");
        for (Identifier e: ids) {
            super.add(e);
        }
        this.ids = ids;

        for (ConstOp e: consts) {
            super.add(e);
        }
        this.consts = consts;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}

    public ArrayList<Identifier> getIds() {
        return ids;
    }

    public void setIds(ArrayList<Identifier> ids) {
        this.ids = ids;
    }

    public TypeOp getType() {
        return type;
    }

    public void setType(TypeOp type) {
        this.type = type;
    }

    public ArrayList<ConstOp> getConsts() {
        return consts;
    }

    public void setConsts(ArrayList<ConstOp> consts) {
        this.consts = consts;
    }
}
