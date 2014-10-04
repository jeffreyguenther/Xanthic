package xanthic;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Assert;
import org.junit.Test;
import xanthic.highlighters.Highlighter;

/**
 *
 */
public class HighlighterTest {

    @Test
    public void test() throws RecognitionException {
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

        String SAMPLE_PROGRAM =
                "def f(x,y) { x = 3+4; y; ; }\n" +
                "def g(x) { return 1+2*x; }\n";

        Grammar g2 = new Grammar(exprGrammar);

        LexerInterpreter g2LexerInterpreter = g2.createLexerInterpreter(new ANTLRInputStream(SAMPLE_PROGRAM));
        CommonTokenStream tokens = new CommonTokenStream(g2LexerInterpreter);
        ParserInterpreter parser = g2.createParserInterpreter(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.parse(g2.rules.get("prog").index);

        for(String s:g2LexerInterpreter.getRuleNames()){
            System.out.println(s);
        }

        for(String s:g2LexerInterpreter.getTokenNames()){
            System.out.println(s);
        }

        for(Token t: g2LexerInterpreter.getAllTokens()){
            String[] tokenNames = g2LexerInterpreter.getRuleNames();
            String tokenName = tokenNames[t.getType() - 1];
            System.out.println(t.getCharPositionInLine() + "  " + tokenName + " start:" + t.getStartIndex() + " end:" + t.getStopIndex());
        }

         String xpath = "//'return'";
        for (ParseTree t : XPath.findAll(tree, xpath, parser) ) {
            System.out.println(t.getSourceInterval());
        }
    }
}
