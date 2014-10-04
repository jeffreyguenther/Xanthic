package xanthic.examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.antlr.v4.tool.Grammar;
import org.fxmisc.richtext.LineNumberFactory;
import xanthic.editor.SyntaxArea;
import xanthic.parsers.Syntax;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class SyntaxAreaExample extends Application{
    SyntaxArea codeArea;

    @Override
    public void start(Stage stage) throws Exception {
        String exprGrammar = "grammar Expr;\n" +
                "prog:   func+ ;\n" +
                "func:  DEF ID '(' arg (',' arg)* ')' body ;\n" +
                "body:  '{' stat+ '}' ;\n" +
                "arg :  ID ;\n" +
                "stat:   expr ';'                 # printExpr\n" +
                "    |   ID '=' expr ';'          # assign\n" +
                "    |   'return' expr ';'        # ret\n" +
                "    |   ';'                      # blank\n" +
                "    ;\n" +
                "expr:   expr ('*'|'/') expr      # MulDiv\n" +
                "    |   expr ('+'|'-') expr      # AddSub\n" +
                "    |   primary                  # prim\n" +
                "    ;\n" +
                "primary" +
                "    :   INT                      # int\n" +
                "    |   ID                       # id\n" +
                "    |   '(' expr ')'             # parens\n" +
                "	 ;" +
                "\n" +
                "MUL :   '*' ; // assigns token name to '*' used above in grammar\n" +
                "DIV :   '/' ;\n" +
                "ADD :   '+' ;\n" +
                "SUB :   '-' ;\n" +
                "RETURN : 'return' ;\n" +
                "DEF:  'def';\n" +
                "ID  :   [a-zA-Z]+ ;      // match identifiers\n" +
                "INT :   [0-9]+ ;         // match integers\n" +
                "NEWLINE:'\\r'? '\\n' -> skip;     // return newlines to parser (is end-statement signal)\n" +
                "WS  :   [ \\t]+ -> skip ; // toss out whitespace\n";
        Grammar grammar = new Grammar(exprGrammar);

        String code =
                "def f(x,y) { x = 3+4; y; ; }\n" +
                "def g(x) { return 1+2*x; }\n";

        Map<String, String> styles = new HashMap<>();
        styles.put("ID", "function-name");
        styles.put("RETURN", "return-stmt");


        Syntax s = new Syntax(grammar, styles);
        codeArea = new SyntaxArea(s);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.replaceText(code);

        codeArea.plainTextChanges().subscribe(System.out::println);

        Scene scene = new Scene(new StackPane(codeArea), 600, 400);
        scene.getStylesheets().add(getClass().getResource("expr-syntax.css").toExternalForm());
        stage.setTitle("Using a grammar loaded at runtime");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void stop() throws Exception {
        // stop the internal thread handling the highlighting
        codeArea.close();
    }
}
