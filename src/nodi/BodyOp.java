package nodi;

import visitors.Visitable;
import visitors.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class BodyOp  extends DefaultMutableTreeNode implements Visitable {

    private ArrayList<DeclOp> vars;
    private ArrayList<StatOp> statOps;

    public BodyOp(ArrayList<DeclOp> vars, ArrayList<StatOp> statOps) {
        super("BodyOp");

        if(vars != null) {
            for (DeclOp e : vars) {
                super.add(e);
            }
            this.vars = vars;
        }

        if(statOps != null) {
            for (StatOp e : statOps) {
                super.add(e);
            }
            this.statOps = statOps;
        }
    }

    public BodyOp addVars(ArrayList<DeclOp> var) {
        if (vars == null) {
            vars = new ArrayList<>();
        }
        vars.addAll(var);
        for (DeclOp e : vars) {
            super.add(e);
        }
        return this;
    }

    public BodyOp addStat(StatOp statOp) {
        if (statOps == null) {
            statOps = new ArrayList<>();
        }
        statOps.add(statOp);
        super.add(statOp);
        return this;
    }



    @Override
    public Object accept(Visitor v) throws Exception {
        return v.visit(this);
    }

    public String toString() {return super.toString();}

    public ArrayList<DeclOp> getVars() {
        return vars;
    }

    public void setVars(ArrayList<DeclOp> vars) {
        this.vars = vars;
    }

    public ArrayList<StatOp> getStatOps() {
        return statOps;
    }

    public void setStatOps(ArrayList<StatOp> statOps) {
        this.statOps = statOps;
    }
}
