package apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class showSVGHtml extends JFrame {
	
	public showSVGHtml(String aSvg) {
		
		StringBuffer contentBuffer = new StringBuffer();
		
		aSvg = aSvg.substring(aSvg.indexOf(">")+1);
		
		contentBuffer.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>SVG Beispiel</title></head>");
		contentBuffer.append("<body><h1>Mein SVG</h1>");
		contentBuffer.append(aSvg);
		contentBuffer.append("</body></html>");
		
		String lufSystem = UIManager.getSystemLookAndFeelClassName();

		try {
			UIManager.setLookAndFeel(lufSystem);
		} catch (Exception e) {

			e.printStackTrace();
		}

		JEditorPane jEditorPane = new JEditorPane();
		jEditorPane.setEditable(false);

		HTMLEditorKit kit = new HTMLEditorKit();
		jEditorPane.setEditorKit(kit);

		StyleSheet styleSheet = kit.getStyleSheet();

		styleSheet.addRule("body {font-family:monospace; font-size: 9px; }");
		styleSheet.addRule("table {width: 100%; border-style: solid; }");
		styleSheet.addRule("th {background-color: gray; }");
		styleSheet.addRule("td {background-color: #dddddd;}");
		styleSheet.addRule("span {background-color: #ffaa55; }");

		Document doc = kit.createDefaultDocument();
		jEditorPane.setDocument(doc);
		jEditorPane.setText(contentBuffer.toString());
		jEditorPane.setAutoscrolls(true);

		JScrollPane jScrollPane = new JScrollPane(jEditorPane);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		jScrollPane.setSize(dim.width * 3 / 4, dim.height * 3 / 4);

		setSize(jScrollPane.getSize());
		add(jScrollPane);
		
		
	}

}
