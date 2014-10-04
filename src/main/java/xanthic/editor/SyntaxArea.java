package xanthic.editor;

import javafx.concurrent.Task;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.tool.Grammar;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.PlainTextChange;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.reactfx.EventStream;
import xanthic.highlighters.ShiroHighlighter;
import xanthic.parsers.ShiroLexer;
import xanthic.parsers.ShiroParser;
import xanthic.parsers.Syntax;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class SyntaxArea extends CodeArea implements AutoCloseable {
    private Syntax syntax;
    private ExecutorService executor;
    private Grammar g;
    EventStream<PlainTextChange> textChanges;

    public SyntaxArea(Syntax s){
        super();
        syntax = s;
        setup();
    }

    public SyntaxArea(Syntax s, String text){
        super(text);
        syntax = s;
        setup();
    }

    private final void setup(){
        executor = Executors.newSingleThreadExecutor();
        g = syntax.getGrammar();

        textChanges = plainTextChanges();

        textChanges.successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(textChanges)
                .subscribe(this::applyHighlighting);
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync(){
        String code = getText();

        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(g, syntax.getStyles(), code);
            }
        };
        executor.execute(task);
        System.out.println("Executing task");
        return task;
    }

    private static StyleSpans<Collection<String>> computeHighlighting(Grammar g, Map<String, String> styles, String text){
        System.out.println("Starting to compute highlighting");
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        if(text.length() > 0){
            LexerInterpreter lex = g.createLexerInterpreter(new ANTLRInputStream(text));
            CommonTokenStream tokenStream = new CommonTokenStream(lex);

            // parse
            int lastEnd = 0;
            for(Token t: lex.getAllTokens()){
                int spacer = t.getStartIndex() - lastEnd;
                if(spacer > 0) {
                    spansBuilder.add(Collections.emptyList(), spacer);

                    int gap = t.getText().length();
                    spansBuilder.add(Collections.singleton(getStyleClass(lex,styles,t)), gap);
                    lastEnd = t.getStopIndex() + 1;
                }
            }
        }else{
            spansBuilder.add(Collections.emptyList(), 0);
        }
        return spansBuilder.create();
    }

    private static String getStyleClass(Lexer lex, Map<String, String> styles, Token t){
        String[] tokenNames = lex.getRuleNames();
        String tokenName = tokenNames[t.getType() - 1];


        String css = styles.get(tokenName);
        if(css == null){
            return "";
        }
        return css;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        if(highlighting.getSpanCount() > 0) {
            setStyleSpans(0, highlighting);
        }
    }

    @Override
    public void close(){
        executor.shutdownNow();
    }

}
