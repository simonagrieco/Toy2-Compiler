package symbolTable;

import nodi.FuncParamsOp;
import nodi.ProcParamsOp;
import nodi.TypeOp;
import java.util.ArrayList;

public class TypeField {

    public static class TypeFieldFunction extends TypeField{
        private ArrayList<String> inputParam;
        private ArrayList<String> outputParam;


        public TypeFieldFunction(){
            this.inputParam = new ArrayList<String>();
            this.outputParam = new ArrayList<String>();
        }

        public TypeFieldFunction (ArrayList<String> inputParam, ArrayList<String> outputParam){
            this.inputParam=inputParam;
            this.outputParam=outputParam;
        }

        public ArrayList<String> getInputParam() {
            return inputParam;
        }

        public void setInputParam(ArrayList<String> inputParam) {
            this.inputParam = inputParam;
        }

        public ArrayList<String> getOutputParam() {
            return outputParam;
        }

        public void setOutputParam(ArrayList<String> outputParam) {
            this.outputParam = outputParam;
        }

        @Override
        public String toString() {
            return "TypeFieldFunction{" +
                    "inputParam=" + inputParam +
                    ", outputParam=" + outputParam +
                    '}';
        }

    }

    public static class TypeFieldProcedure extends TypeField{
        private ArrayList<String> inputParam;

        public TypeFieldProcedure() {
            inputParam = new ArrayList<>();
        }

        public TypeFieldProcedure(ArrayList<String> inputParam) {
            this.inputParam = inputParam;
        }

        public TypeFieldProcedure(String typeOp) {
            super();
        }

        public ArrayList<String> getInputParam() {
            return inputParam;
        }

        public void setInputParam(ArrayList<String> inputParam) {
            this.inputParam = inputParam;
        }

        @Override
        public String toString() {
            return "TypeFieldProcedure{" +
                    "inputParam=" + inputParam +
                    '}';
        }
    }

    public static class TypeFieldVar extends TypeField{
        private String type;

        public TypeFieldVar(String type){
            this.type=type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String toString(){
            return type;
        }
    }
}

