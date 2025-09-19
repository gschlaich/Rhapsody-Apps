package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.*;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;



/**
 * Kleines Demo-Fenster: Markdown editieren & Vorschau.
 */
public class MarkdownEditorPreview extends JDialog
{

	private final Parser       parser   = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private final RSyntaxTextArea textArea  = new RSyntaxTextArea();
    private final JEditorPane     preview   = new JEditorPane("text/html", "");
	
	private boolean okPressed = false;  
	
	public MarkdownEditorPreview(Window owner, String initialText)
	{
		super(owner, "Markdown bearbeiten", ModalityType.APPLICATION_MODAL);
		
		
		CompletionProvider provider = createMarkdownProvider();
		AutoCompletion ac = new AutoCompletion(provider);

		ac.setAutoActivationEnabled(true);        // Popup beim Tippen
		ac.setAutoActivationDelay(250);           // in ms
		ac.setTriggerKey(KeyStroke.getKeyStroke(
		        KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK)); // Strg-Leertaste

		ac.install(textArea);                     // textArea = dein RSyntaxTextArea
		
		
		buildUI(initialText);
		pack();
		setMinimumSize(new Dimension(700, 500));
		setLocationRelativeTo(owner);
	}
	
	/** Öffnet den Dialog modal und gibt Markdown zurück oder null bei Cancel. */
    public static String showDialog(Window owner, String initialText) {
        MarkdownEditorPreview dlg = new MarkdownEditorPreview(owner, initialText);
        
        dlg.setVisible(true);                   // blockiert bis dispose()
        return dlg.okPressed ? dlg.textArea.getText() : null;
    }

	
	
	
	 private void buildUI(String initialText) {
	        // SplitPane (Editor & Preview) ins Zentrum
	        getContentPane().add(buildSplitPane(initialText), BorderLayout.CENTER);

	        // Button-Leiste unten
	        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	        JButton ok     = new JButton("OK");
	        JButton cancel = new JButton("Abbrechen");

	        ok.addActionListener(e -> { okPressed = true; dispose(); });
	        cancel.addActionListener(e -> dispose());

	        // Enter = OK, Esc = Cancel
	        getRootPane().setDefaultButton(ok);
	        getRootPane().registerKeyboardAction(
	            e -> dispose(),
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	        buttonBar.add(cancel);
	        buttonBar.add(ok);
	        getContentPane().add(buttonBar, BorderLayout.SOUTH);
	    }
	 
	 private JSplitPane buildSplitPane(String initialText) {
	        /* --- Editor -------------------------------------------------------- */
	        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
	        textArea.setCodeFoldingEnabled(true);
	        textArea.setText(initialText == null ? "" : initialText);

	        textArea.getDocument().addDocumentListener(new DocumentListener() {
	            public void insertUpdate(DocumentEvent e) { updatePreview(); }
	            public void removeUpdate(DocumentEvent e) { updatePreview(); }
	            public void changedUpdate(DocumentEvent e) { updatePreview(); }
	        });

	        RTextScrollPane editorScroll = new RTextScrollPane(textArea);
	        editorScroll.setFoldIndicatorEnabled(true);

	        /* --- Preview ------------------------------------------------------- */
	        preview.setEditable(false);
	        updatePreview();

	        /* --- SplitPane ----------------------------------------------------- */
	        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
	                                          editorScroll,
	                                          new JScrollPane(preview));
	        split.setResizeWeight(0.5);
	        return split;
	    }



	
	/** Rendert den Editor-Inhalt als HTML und zeigt ihn an. */
	 private void updatePreview() 
	 {
	        
		 MutableDataSet opts = new MutableDataSet()
				 .set(Parser.EXTENSIONS,
					     java.util.Collections.singletonList(TablesExtension.create()));

			Parser parser = Parser.builder(opts).build();
		 
			HtmlRenderer renderer = HtmlRenderer.builder(opts)
			        .nodeRendererFactory(options -> new PlantUmlNodeRenderer())
			        .build();

		 
		 Node ast   = parser.parse(textArea.getText());
	        String html = renderer.render(ast);
	        
	       

	        preview.setText(
	            "<!doctype html><html><head><meta charset='utf-8'>"
	          + "<style>body{font-family:sans-serif;padding:12px}"
	          + "code,pre{background:#f5f5f5;padding:2px 4px}</style>"
	          + "</head><body>" + html + "</body></html>");
	          
	        
	        //System.out.println(preview.getText());
	        
	        preview.setCaretPosition(0);
	        
	        
	    }
	 
	 private CompletionProvider createMarkdownProvider() {
		    DefaultCompletionProvider cp = new DefaultCompletionProvider();

		    /* --- Bisherige Einträge (**, *, code, link, tbl …) --- */

		    // 1–6: Überschriften
		    for (int i = 1; i <= 6; i++) {
		        String trigger = "h" + i;
		        String hashes  = hashes(i); 
		        cp.addCompletion(new ShorthandCompletion(
		                cp, trigger, hashes + " ${cursor}", "Überschrift H" + i));
		    }

		    cp.addCompletion(new ShorthandCompletion(cp, ">",  "> ${cursor}",  "Blockquote"));
		    cp.addCompletion(new ShorthandCompletion(cp, "hr", "---",          "Horizontale Linie"));
		    cp.addCompletion(new ShorthandCompletion(cp, "li", "- ${cursor}",  "Bullet List-Item"));
		    cp.addCompletion(new ShorthandCompletion(cp, "ol", "1. ${cursor}", "Nummerierte Liste"));
		    cp.addCompletion(new ShorthandCompletion(cp, "todo", "- [ ] ${cursor}", "Task List-Item"));

		    cp.addCompletion(new ShorthandCompletion(cp, "img",
		            "![${cursor}](url)", "Bild"));
		    cp.addCompletion(new ShorthandCompletion(cp, "url",
		            "[${cursor}](https://)", "Link"));
		    cp.addCompletion(new ShorthandCompletion(cp, "`ci",
		            "`${cursor}`", "Inline-Code"));
		    cp.addCompletion(new ShorthandCompletion(cp, "del",
		            "~~${cursor}~~", "Durchgestrichen"));
		    
		    cp.addCompletion(new ShorthandCompletion(
		            cp,
		            "puml",                               // Trigger
		            "```plantuml\n@startuml\n${cursor}\n@enduml\n```",
		            "PlantUML-Diagramm"));

		    // Code-Block + Sprache
		    String[] langs = { "java", "js", "xml", "json", "bash", "cpp" };
		    for (String lang : langs) {
		        cp.addCompletion(new ShorthandCompletion(cp, lang,
		                "```" + lang + "\n${cursor}\n```", "Code-Block " + lang));
		    }

		    // Tabelle 2×3
		    cp.addCompletion(new ShorthandCompletion(cp, "tbl2",
		            "| Spalte 1 | Spalte 2 |\n|---------|---------|\n| ${cursor} | |\n| | |",
		            "Tabelle 2 × 3"));

		    // Fußnote
		    cp.addCompletion(new ShorthandCompletion(cp, "fn",
		            "[^1]\n\n[^1]: ${cursor}", "Fußnote"));

		    // Front-Matter
		    cp.addCompletion(new ShorthandCompletion(cp, "fm",
		            "---\nlayout: default\ntitle: \"${cursor}\"\n---", "Front-Matter"));

		    // BasicCompletions (reine Tokens) – falls du Autopopup beim Tippen willst
		    cp.addCompletion(new BasicCompletion(cp, "#"));
		    cp.addCompletion(new BasicCompletion(cp, "##"));
		    /* … */

		    return cp;
		}
	 
	 private static String hashes(int count)
	 {
		    StringBuilder sb = new StringBuilder(count);
		    for (int j = 0; j < count; j++) sb.append('#');
		    return sb.toString();
		}

}





