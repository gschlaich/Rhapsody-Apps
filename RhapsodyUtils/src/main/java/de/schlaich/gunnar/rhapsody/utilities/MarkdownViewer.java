package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class MarkdownViewer extends JDialog
{
	private final JEditorPane viewer = new JEditorPane("text/html", "");
	private final JScrollPane scrollPane = new JScrollPane(viewer);

	/** JNA-Interface für DWM Dark-Title-Bar */
	interface Dwmapi extends StdCallLibrary
	{
		Dwmapi INSTANCE = Native.load("dwmapi", Dwmapi.class);
		int DwmSetWindowAttribute(HWND hwnd, int dwAttribute, com.sun.jna.ptr.IntByReference pvAttribute, int cbAttribute);
	}

	public MarkdownViewer(Window owner, String markdownText)
	{
		super(owner, "View Markdown", ModalityType.MODELESS);

		buildUI(markdownText);
		pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int maxWidth = (int) (screen.width * 0.5);
		int maxHeight = (int) (screen.height * 0.8);
		setSize(Math.min(getWidth(), maxWidth), Math.min(getHeight(), maxHeight));
		setMinimumSize(new Dimension(700, 500));
		setLocationRelativeTo(owner);
	}

	public static void ShowDialog(Window owner, String markdownText)
	{
		MarkdownViewer dlg = new MarkdownViewer(owner, markdownText);
		dlg.setVisible(true);
		if (dlg.isWindowsDarkMode())
		{
			dlg.applyDarkTitleBar();
		}
	}

	/** Setzt den nativen Fensterrahmen auf Dark Mode via DWM API. */
	private void applyDarkTitleBar()
	{
		try
		{
			HWND hwnd = new HWND(Native.getComponentPointer(this));
			com.sun.jna.ptr.IntByReference darkMode = new com.sun.jna.ptr.IntByReference(1);
			// DWMWA_USE_IMMERSIVE_DARK_MODE = 20
			Dwmapi.INSTANCE.DwmSetWindowAttribute(hwnd, 20, darkMode, 4);
		}
		catch (Exception e)
		{
			// ignore if not supported
		}
	}

	private void buildUI(String initialText)
	{
		boolean dark = isWindowsDarkMode();

		JPanel centerPanel = new JPanel(new BorderLayout());
		viewer.setEditable(false);

		if (dark)
		{
			Color bg = new Color(0x1e1e1e);
			Color thumb = new Color(0x555555);
			Color track = new Color(0x2d2d2d);

			scrollPane.getViewport().setBackground(bg);
			scrollPane.setBackground(bg);
			scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());

			scrollPane.getVerticalScrollBar().setBackground(track);
			scrollPane.getVerticalScrollBar().setForeground(thumb);
			scrollPane.getHorizontalScrollBar().setBackground(track);
			scrollPane.getHorizontalScrollBar().setForeground(thumb);

			scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI()
			{
				@Override protected void configureScrollBarColors()
				{
					thumbColor = thumb;
					trackColor = track;
				}
			});
			scrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI()
			{
				@Override protected void configureScrollBarColors()
				{
					thumbColor = thumb;
					trackColor = track;
				}
			});
		}

		centerPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		if (dark)
		{
			buttonBar.setBackground(new Color(0x2e2e2e));
		}
		JButton ok = new JButton("OK");
		
		if(dark)
		{
			ok.setBackground(new Color(0x3e3e3e));		
		}
		
		ok.addActionListener(e -> dispose());
		

		getRootPane().setDefaultButton(ok);
		getRootPane().registerKeyboardAction(e -> dispose(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		buttonBar.add(ok);
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(buttonBar, BorderLayout.SOUTH);
		updatePreview(initialText);
	}

	/** Erkennt ob Windows im Dark Mode läuft. */
	private boolean isWindowsDarkMode()
	{
		try
		{
			Process process = Runtime.getRuntime().exec(
				"reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\" /v AppsUseLightTheme");
			java.util.Scanner scanner = new java.util.Scanner(process.getInputStream());
			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				if (line.contains("AppsUseLightTheme"))
					return line.trim().endsWith("0x0");
			}
		}
		catch (Exception e)
		{
			// fallback to light
		}
		return false;
	}

	/** Rendert den Markdown-Inhalt als HTML und zeigt ihn an. */
	private void updatePreview(String markdownText)
	{
		MutableDataSet opts = new MutableDataSet().set(Parser.EXTENSIONS,
				java.util.Collections.singletonList(TablesExtension.create()));

		Parser parser = Parser.builder(opts).build();
		HtmlRenderer renderer = HtmlRenderer.builder(opts)
				.nodeRendererFactory(options -> new PlantUmlNodeRenderer())
				.build();

		Node ast = parser.parse(markdownText);
		String html = renderer.render(ast);

		String style;
		if (isWindowsDarkMode())
		{
			style = "body{font-family:sans-serif;padding:12px;background:#1e1e1e;color:#d4d4d4}"
				  + "code,pre{background:#2d2d2d;color:#ce9178;padding:2px 4px}"
				  + "a{color:#4ec9b0}"
				  + "h1,h2,h3,h4,h5,h6{color:#9cdcfe}"
				  + "table{border-collapse:collapse}td,th{border:1px solid #555;padding:4px 8px}"
				  + "th{background:#2d2d2d}tr:nth-child(even){background:#252525}";
		}
		else
		{
			style = "body{font-family:sans-serif;padding:12px}"
				  + "code,pre{background:#f5f5f5;padding:2px 4px}"
				  + "table{border-collapse:collapse}td,th{border:1px solid #ccc;padding:4px 8px}"
				  + "th{background:#f0f0f0}tr:nth-child(even){background:#fafafa}";
		}

		viewer.setText("<!doctype html><html><head><meta charset='utf-8'>"
				+ "<style>" + style + "</style>"
				+ "</head><body>" + html + "</body></html>");
		viewer.setCaretPosition(0);
	}
}