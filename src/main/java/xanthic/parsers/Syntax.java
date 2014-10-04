package xanthic.parsers;

import org.antlr.v4.tool.Grammar;

import java.util.Map;

/**
 * Describes the syntax for a SyntaxArea
 */
public class Syntax {
    private Map<String, String> styles;
    private Grammar grammar;

    public Syntax(Grammar grammar, Map<String, String> styleMap){
        this.grammar = grammar;
        styles = styleMap;
    }

    public Map<String, String> getStyles() {
        return styles;
    }

    public Grammar getGrammar() {
        return grammar;
    }
}
