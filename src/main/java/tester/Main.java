package main.java.tester;
import main.Toy2Lexer;
import main.parser;
import nodi.ProgramOp;
import visitors.CodeGenerationVisitor;
import visitors.ScopeVisitor;
import visitors.TypeVisitor;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("File mancante");
        }
        String filePath = args[0];
        File file = new File(filePath);

        FileInputStream stream = new FileInputStream(filePath);
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        Toy2Lexer scanner = new Toy2Lexer(reader);
        parser p = new parser(scanner);
        //JTree tree;

        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) p.parse().value;

            //per mostrare l'albero
            /*tree=new JTree(root);
            JFrame framePannello=new JFrame();
            framePannello.setSize(400, 400);
            JScrollPane treeView = new JScrollPane(tree);
            framePannello.add(treeView);
            framePannello.setVisible(true);*/

            ((ProgramOp) root).accept(new ScopeVisitor());
            ((ProgramOp) root).accept(new TypeVisitor());
            ((ProgramOp) root).accept(new CodeGenerationVisitor(file.getName().substring(0, file.getName().lastIndexOf('.'))));
        } catch (Exception e) {
            System.out.println("Errore di compilazione nel file: " + filePath + "\n" + e.getMessage());
        }

    }
}
