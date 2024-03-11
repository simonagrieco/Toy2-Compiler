package nodi;

import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;

public class FuncParamsOp extends DefaultMutableTreeNode implements Visitable {
    Identifier id;
    TypeOp type;

    boolean isOutput;

    public FuncParamsOp(Identifier id, TypeOp type) {
        super("FuncParamsOp");

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
    public TypeOp getTypeOp() {
        return type;
    }

    public void setTypeOp(TypeOp type) {
        this.type = type;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public void setOutput(boolean output) {
        isOutput = output;
    }
    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}

}
