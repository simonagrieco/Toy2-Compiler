package symbolTable;

public class RowTable {

    private String symbol;
    private Object kind;
    private TypeField type;
    private String properties;

    public RowTable(String symbol, Object kind, TypeField type, String properties){
        this.symbol=symbol;
        this.kind=kind;
        this.type=type;
        this.properties=properties;
    }

    public RowTable(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public Object getKind() {
        return kind;
    }

    public TypeField getType() {
        return type;
    }

    public String getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        String typeString;
        if (type.getClass()== TypeField.TypeFieldFunction.class)
            typeString=((TypeField.TypeFieldFunction) type).toString();
        else if (type.getClass()== TypeField.TypeFieldProcedure.class)
            typeString=((TypeField.TypeFieldProcedure) type).toString();
        else
            typeString=((TypeField.TypeFieldVar) type).toString();

        return "Symbol: "+symbol+", Kind: "+kind.toString()+", Type: "+typeString+", Properties: "+properties;
    }
}
