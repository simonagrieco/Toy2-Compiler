package symbolTable;

import nodi.Identifier;

import java.util.*;

public class SymbolTable {

    // Riferimento alla tabella dei simboli padre
    private SymbolTable father;
    private String scope;  // Nome dello scope associato alla tabella dei simboli
    private ArrayList<RowTable> listRow; // Lista di righe (oggetti RowTable) contenenti informazioni sui simboli
    private static boolean shadowing = true; // Flag per attivare o disattivare la regola dello shadowing

    public SymbolTable() {
        father = null;
        listRow = new ArrayList<RowTable>();
    }

    // Costruttore con parametri per creare una tabella dei simboli con padre, lista di righe e scope specifici
    public SymbolTable(SymbolTable father, ArrayList<RowTable> listRow, String scope) {
        this.father = father;
        this.listRow = listRow;
        this.scope = scope;
    }

    // Metodo per aggiungere una riga alla tabella dei simboli
    public void addRow(RowTable row) throws Exception {

        // Controllo se lo shadowing è attivo
        if (shadowing) {
            // Se lo shadowing è attivo, controllo se c'è già una dichiarazione del simbol o se il simbolo è lo scope stesso (tranne che per lo scope "Root")
            if (symbolcheck(row.getSymbol()) || (row.getSymbol().equals(scope) && !row.getSymbol().equals("Root")))
                throw new RuntimeException("Dichiarazione multipla!"); // Eccezione se c'è una dichiarazione multipla
            else
                listRow.add(row); // Aggiungo la riga alla lista
        } else {
            // Se lo shadowing è disattivato, devo fare la verifica in tutte le tabelle dei simboli superiori
            SymbolTable symbolTable = this;
            while (symbolTable != null) {
                if (symbolTable.symbolcheck(row.getSymbol()) || (row.getSymbol().equals(symbolTable.scope) && !row.getSymbol().equals("Root")))
                    throw new RuntimeException("Dichiarazione multipla!"); // Eccezione se c'è una dichiarazione multipla
                else
                    symbolTable = symbolTable.getFather(); // Passo alla tabella dei simboli superiore
            }
            listRow.add(row); // Aggiungo la riga alla lista
        }
    }

    // Metodo per verificare se un simbolo è presente nella tabella dei simboli corrente
    public boolean symbolcheck(String x) {
        for (RowTable r : this.listRow)
            if (r.getSymbol().equals(x))
                return true; // Restituisce true se il simbolo è presente
        return false; // Restituisce false se il simbolo non è presente
    }

    // Metodo per cercare una riga specifica (simbolo) nella tabella dei simboli corrente e nelle tabelle dei simboli superiori
    public RowTable lookup(String id) {
        SymbolTable symbolTable = this;
        while (symbolTable != null) {
            for (RowTable r : symbolTable.listRow) {
                if (r.getSymbol().equals(id))
                    return r; // Restituisce la riga se il simbolo è trovato
            }
            symbolTable = symbolTable.father; // Passo alla tabella dei simboli superiore
        }
        return null; // Restituisce null se il simbolo non è trovato
    }

    // Metodo per impostare il padre della tabella dei simboli
    public void setFather(SymbolTable father) {
        this.father = father;
    }

    // Metodo per ottenere il padre della tabella dei simboli
    public SymbolTable getFather() {
        return this.father;
    }

    // Metodo per ottenere la lista di righe (simboli) nella tabella dei simboli corrente
    public ArrayList<RowTable> getListRow() {
        return this.listRow;
    }

    // Metodo per impostare lo scope della tabella dei simboli
    public void setScope(String scope) {
        this.scope = scope;
    }

    // Metodo per ottenere lo scope della tabella dei simboli
    public String getScope() {
        return scope;
    }

    // Metodo per ottenere il tipo di ritorno di una funzione, se la tabella dei simboli corrente è associata a una funzione
   /*
    public String getTypeFun() {
        SymbolTable currentScope = this;
        SymbolTable padre = currentScope.getFather();
        while (!padre.getScope().equals("Root")) {
            currentScope = padre;
            padre = currentScope.getFather();
        }

        // Mi trovo con la currentScope nella funzione che contiene il return
        // Mi trovo col padre dell currentScope nella Root
        String scopeName = currentScope.getScope(); // Coincide col nome della funzione
        RowTable row = padre.lookup(scopeName); // Se la lookup fallisce ho un errore in semantica 1
        if (row != null) {
            TypeField.TypeFieldFunction typeFieldFunction = (TypeField.TypeFieldFunction) row.getType();
            var typeOutput = typeFieldFunction.getParams().get(0);

            return typeOutput;
        } else {
            return null;
        }
    } */

    // Override del metodo toString per rappresentare la tabella dei simboli come stringa
    @Override
    public String toString() {
        String str = "SymbolTable: " + scope + "\n";
        if (father != null) str = str + "Father: " + father.getScope() + "\n";
        for (RowTable row : listRow)
            str = str + row.toString() + "\n";
        return str;
    }


}
