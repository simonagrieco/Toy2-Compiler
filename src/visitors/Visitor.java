package visitors;

import nodi.*;
public interface Visitor {

    public Object visit(BodyOp bodyOp) throws Exception;
    public Object visit(ConstOp constOp) throws Exception;
    public Object visit(DeclOp declOp) throws Exception;
    public Object visit(ElifOp elifOp) throws Exception;
    public Object visit(ExprOp exprOp) throws Exception;
    public Object visit(FunCallOp funCallOp) throws Exception;
    public Object visit(FuncNodeOp funcNodeOp) throws Exception;
    public Object visit(FuncParamsOp funcParamsOp) throws Exception;
    public Object visit(Identifier identifier) throws Exception;
    public Object visit(IfStatOp ifStatOp) throws Exception;
    public Object visit(IterOp iterOp) throws Exception;
    public Object visit(ProcCallOp procCallOp) throws Exception;
    public Object visit(ProcNodeOp procNodeOp) throws Exception;
    public Object visit(ProcParamsOp procParamsOp) throws Exception;
    public Object visit(ProgramOp programOp) throws Exception;
    public Object visit(StatOp statOp) throws Exception;
    public Object visit(TypeOp typeOp) throws Exception;
    public Object visit(UnaryOp unaryOp) throws Exception;
    public Object visit(WhileOp whileOp) throws Exception;

}
