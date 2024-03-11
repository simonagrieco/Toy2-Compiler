package nodi;

import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;

public class ProcParamsOp extends DefaultMutableTreeNode implements Visitable {
    Identifier id;
    TypeOp type;

    public ProcParamsOp(Identifier id, TypeOp type) {
        super("ProcParamsOp");

        this.id = id;
        this.type = type;

        super.add(id);
        super.add(type);
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public TypeOp getType() {
        return type;
    }

    public void setType(TypeOp type) {
        this.type = type;
    }

    public TypeOp getTypeOp() {
        return type;
    }

    public void setTypeOp(TypeOp type) {
        this.type = type;
    }

    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}
}
