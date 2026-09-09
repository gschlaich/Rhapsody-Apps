package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.FindToolBar;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.SizeGripIcon;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import com.formdev.flatlaf.FlatDarkLaf;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPHyperLink;

public class XmlEditor extends JDialog implements SearchListener
{
	private final RSyntaxTextArea textArea = new RSyntaxTextArea();
	private final File xmlFile;
	private boolean okPressed = false;
	private Charset xmlCharset = StandardCharsets.UTF_8;
	private JPanel titleBar;
	private JPanel buttonBar;
	private RTextScrollPane scrollPane;
	private SyntaxScheme myScheme;

	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private FindToolBar findToolBar;
	private ReplaceToolBar replaceToolBar;
	
	static private IRPHyperLink HyperLink = null;
	static private IRPApplication Rhapsody = null;

	private CollapsibleSectionPanel csp;

	public XmlEditor(Window owner, File xmlFile)
	{
		super(owner, xmlFile == null ? "Edit XML" : "Edit XML - " + xmlFile.getName(),
				Dialog.ModalityType.APPLICATION_MODAL);
		this.xmlFile = xmlFile;

		

		if (RhapsodyPreferences.isWindowsDarkMode())
		{
			try
			{
				UIManager.setLookAndFeel(new FlatDarkLaf());
				// setUndecorated(true);
			}
			catch(UnsupportedLookAndFeelException e)
			{
				e.printStackTrace();
			}
		}
		
		initSearchDialogs();

		buildUI();
		if (RhapsodyPreferences.isWindowsDarkMode())
		{
			setDarkStyle();
		}
		setMinimumSize(new Dimension(700, 500));

		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int maxHeight = (int) (screen.getHeight() * 0.8);
		int maxWidth = (int) (screen.getWidth() * 0.8);

		Dimension preferred = getPreferredSize();
		int width = Math.min(preferred.width, maxWidth);
		int height = Math.min(preferred.height, maxHeight);
		setPreferredSize(new Dimension(width, height));
		pack();
		setLocationRelativeTo(owner);

	}

	private void addItem(Action a, ButtonGroup bg, JMenu menu)
	{
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
		bg.add(item);
		menu.add(item);
	}

	private JMenuBar createMenuBar()
	{

		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu("Search");
		menu.add(new JMenuItem(new ShowFindDialogAction()));
		menu.add(new JMenuItem(new ShowReplaceDialogAction()));
		menu.add(new JMenuItem(new GoToLineAction()));
		menu.addSeparator();

		int ctrl = getToolkit().getMenuShortcutKeyMask();
		int shift = InputEvent.SHIFT_MASK;
		// KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl | shift);
		// Action a = csp.addBottomComponent(ks, findToolBar);
		// a.putValue(Action.NAME, "Show Find Search Bar");
		// menu.add(new JMenuItem(a));
		// ks = KeyStroke.getKeyStroke(KeyEvent.VK_H, ctrl | shift);
		// a = csp.addBottomComponent(ks, replaceToolBar);
		// a.putValue(Action.NAME, "Show Replace Search Bar");
		// menu.add(new JMenuItem(a));

		mb.add(menu);

		return mb;

	}

	public XmlEditor(Window owner, String initialText)
	{
		this(owner, (File) null);
		textArea.setText(initialText == null ? "" : initialText);
	}

	public static void showDialog(Window owner, File xmlFile)
	{
		XmlEditor dlg = new XmlEditor(owner, xmlFile);
		dlg.setVisible(true);
	}
	
	
	public static void showDialog(Window owner, String initialText)
	{
		XmlEditor dlg = new XmlEditor(owner, initialText);
		dlg.setVisible(true);
	}

	public static void showDialogAsync(Window owner, File xmlFile)
	{
		showDialogAsync(owner, xmlFile, null);
	}

	public static void showDialogAsync(Window owner, String initialText, Consumer<String> resultHandler)
	{
		Thread editorThread = new Thread(() ->
		{
			try
			{
				SwingUtilities.invokeAndWait(() ->
				{
					XmlEditor dlg = new XmlEditor(owner, initialText);
					dlg.addWindowListener(new java.awt.event.WindowAdapter()
					{
						@Override
						public void windowClosed(java.awt.event.WindowEvent e)
						{
							if (resultHandler != null)
							{
								resultHandler.accept(dlg.okPressed ? dlg.textArea.getText() : null);
							}
						}
					});
					dlg.setVisible(true);
				});
			}
			catch(Exception ex)
			{
				if (resultHandler != null)
				{
					resultHandler.accept(null);
				}
			}
		}, "XmlEditor");
		editorThread.setDaemon(true);
		editorThread.start();
	}

	public static void showDialogAsync(Window owner, File xmlFile, Consumer<String> resultHandler)
	{
		Thread editorThread = new Thread(() ->
		{
			try
			{
				SwingUtilities.invokeAndWait(() ->
				{
					XmlEditor dlg = new XmlEditor(owner, xmlFile);
					dlg.addWindowListener(new java.awt.event.WindowAdapter()
					{
						@Override
						public void windowClosed(java.awt.event.WindowEvent e)
						{
							if (resultHandler != null)
							{
								resultHandler.accept(dlg.okPressed ? dlg.textArea.getText() : null);
							}
						}
					});
					dlg.setVisible(true);
				});
			}
			catch(Exception ex)
			{
				if (resultHandler != null)
				{
					resultHandler.accept(null);
				}
			}
		}, "XmlEditor");
		editorThread.setDaemon(true);
		editorThread.start();
	}

	public static boolean openHyperLink(IRPHyperLink link, IRPApplication aRhapsody)
	{
		if (link == null)
		{
			return false;
		}
		
		HyperLink = link;
		
		Rhapsody = aRhapsody;

		String absolutePath = RhapsodyHelper.getAbsolutePath(link);
		if (absolutePath == null)
		{
			return false;
		}

		String lower = absolutePath.toLowerCase(Locale.ROOT);
		if (!lower.endsWith(".xml"))
		{
			return false;
		}

		File xmlFile = new File(absolutePath);
		if (!xmlFile.exists())
		{
			return false;
		}

		showDialog(null, xmlFile);
		return true;
	}

	private void initSearchDialogs()
	{

		findDialog = new FindDialog(this, this);

		replaceDialog = new ReplaceDialog(this, this);

		// This ties the properties of the two dialogs together (match case,
		// regex, etc.).
		SearchContext context = findDialog.getSearchContext();
		replaceDialog.setSearchContext(context);

		// Create toolbars and tie their search contexts together also.
		findToolBar = new FindToolBar(this);
		findToolBar.setSearchContext(context);
		replaceToolBar = new ReplaceToolBar(this);
		replaceToolBar.setSearchContext(context);

	}

	private void buildUI()
	{
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		textArea.setCodeFoldingEnabled(true);
		textArea.setAntiAliasingEnabled(true);
		textArea.setHighlightCurrentLine(true);
		textArea.setMarkOccurrences(true);
		textArea.setTabsEmulated(true);
		textArea.setTabSize(4);
		textArea.setCloseCurlyBraces(true);
		textArea.setCloseMarkupTags(true);
		textArea.setLineWrap(false);
		textArea.setAutoIndentEnabled(true);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		textArea.setCaretPosition(0);

		LanguageSupportFactory.get().register(textArea);

		loadXmlContent();

		scrollPane = new RTextScrollPane(textArea);
		scrollPane.setFoldIndicatorEnabled(true);
		scrollPane.setWheelScrollingEnabled(true);

		buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton copyButton = new JButton("Copy to Appdata");
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");

		okButton.addActionListener(e ->
		{
			okPressed = true;
			if (xmlFile != null)
			{
				saveXmlContent();
			}
			dispose();
		});

		cancelButton.addActionListener(e -> dispose());

		copyButton.addActionListener(e ->
		{
			
			if(HyperLink == null)
			{
				JOptionPane.showMessageDialog(this, "No Hyperlink available!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			//HyperLink.get
			
			USMConfiguration config = USMConfiguration.Instance(Rhapsody, null);
			
			config.copyToAppData(HyperLink);

		});

		
		buttonBar.add(copyButton);
		buttonBar.add(okButton);
		buttonBar.add(cancelButton);
		
		

		JPanel content = new JPanel(new BorderLayout());
		if (RhapsodyPreferences.isWindowsDarkMode())
		{
			// JLabel titleLabel = new JLabel(getTitle());
			// titleLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
			// titleLabel.setForeground(new Color(0xdddddd));
			// titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 12f));

			// titleBar = new JPanel(new BorderLayout());
			// titleBar.setBackground(new Color(0x232323));
			// titleBar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0,
			// new Color(0x444444)));
			// titleBar.add(titleLabel, BorderLayout.WEST);

			//JButton closeButton = new JButton("X");
			//closeButton.setFocusable(false);
			//closeButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
			//closeButton.setBackground(new Color(0x232323));
			//closeButton.setForeground(new Color(0xdddddd));
			//closeButton.addActionListener(e -> dispose());
			// titleBar.add(closeButton, BorderLayout.EAST);

			// content.add(titleBar, BorderLayout.NORTH);
		}

		content.add(scrollPane, BorderLayout.CENTER);
		content.add(buttonBar, BorderLayout.SOUTH);

		// csp = new CollapsibleSectionPanel();
		// content.add(csp);

		setJMenuBar(createMenuBar());

		// getContentPane().setLayout(new BorderLayout());
		getContentPane().add(content, BorderLayout.CENTER);
	}

	private void loadXmlContent()
	{
		if (xmlFile == null || xmlFile.exists() == false)
		{
			return;
		}

		try
		{
			byte[] bytes = Files.readAllBytes(xmlFile.toPath());
			xmlCharset = detectXmlCharset(bytes);
			String xmlText = new String(bytes, xmlCharset);
			textArea.setText(xmlText);
		}
		catch(IOException e)
		{
			textArea.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<!-- error reading file -->\n");
		}
	}

	private static Charset detectXmlCharset(byte[] bytes)
	{
		if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF)
		{
			return StandardCharsets.UTF_8;
		}
		if (bytes.length >= 2 && bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE)
		{
			return StandardCharsets.UTF_16LE;
		}
		if (bytes.length >= 2 && bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF)
		{
			return StandardCharsets.UTF_16BE;
		}

		CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder();
		utf8Decoder.onMalformedInput(CodingErrorAction.REPORT);
		utf8Decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		try
		{
			utf8Decoder.decode(ByteBuffer.wrap(bytes));
			return StandardCharsets.UTF_8;
		}
		catch(CharacterCodingException ignored)
		{
			// fall through to XML declaration parsing below
		}

		String text = new String(bytes, StandardCharsets.ISO_8859_1);
		Matcher matcher = Pattern.compile("(?is)<?xml.*?encoding\\s*=\\s*(['\"])(.*?)\\1").matcher(text);
		if (matcher.find())
		{
			String enc = matcher.group(2).trim();
			try
			{
				return Charset.forName(enc);
			}
			catch(Exception ignored)
			{
				// keep fallback
			}
		}

		return StandardCharsets.UTF_8;
	}

	private void saveXmlContent()
	{
		if (xmlFile == null)
		{
			return;
		}

		try
		{
			Files.write(xmlFile.toPath(), textArea.getText().getBytes(xmlCharset));
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	private void setDarkStyle()
	{
		// settings should be similar to rhapsody
		// change to dark mode for the gui
		Color fg = new Color(0xdddddd);
		Color border = new Color(0x444444);
		Color gutterBg = new Color(0x232323);

		// use same font as rhapsody
		textArea.setFont(new Font("Courier New", Font.PLAIN, 14));

		textArea.setMarginLineColor(new Color(0xCCCCCC));
		textArea.setMarkAllHighlightColor(new Color(0x214283));
		textArea.setMatchedBracketBorderColor(new Color(0x555555));
		textArea.setBracketMatchingEnabled(true);
		textArea.setMatchedBracketBGColor(new Color(0x999999));
		myScheme = textArea.getSyntaxScheme();

		// set for dark mode
		textArea.setForeground(new Color(0xAAAAB0));
		textArea.setBackground(new Color(0x1F1F1F));
		textArea.setCaretColor(new Color(0xCCCCCC));
		textArea.setCurrentLineHighlightColor(new Color(0x373737));
		textArea.setFadeCurrentLineHighlight(false);

		textArea.setSelectionColor(new Color(0x777777));

		textArea.setMarkOccurrencesColor(new Color(0x373737));

		textArea.setCloseCurlyBraces(true);

		int colorComment = 0x808080;

		setTokenFgColor(SyntaxScheme.IDENTIFIER, 0xCCCCCC);
		setTokenFgColor(SyntaxScheme.RESERVED_WORD, 0xFF0000);
		setTokenFgColor(SyntaxScheme.RESERVED_WORD_2, 0xFF0000);
		setTokenFgColor(SyntaxScheme.ANNOTATION, 0xFF0000);
		setTokenFgColor(SyntaxScheme.COMMENT_DOCUMENTATION, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_EOL, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_DOCUMENTATION, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_MULTILINE, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_MARKUP, colorComment);
		setTokenFgColor(SyntaxScheme.COMMENT_KEYWORD, colorComment);
		setTokenFgColor(SyntaxScheme.DATA_TYPE, 0x00FF00);
		setTokenFgColor(SyntaxScheme.VARIABLE, 0xFF0000);
		setTokenFgColor(SyntaxScheme.LITERAL_STRING_DOUBLE_QUOTE, 0x17C6A3);
		setTokenFgColor(SyntaxScheme.LITERAL_CHAR, 0x17C6A3);
		setTokenFgColor(SyntaxScheme.LITERAL_BACKQUOTE, 0x17C6A3);
		setTokenFgColor(SyntaxScheme.LITERAL_NUMBER_DECIMAL_INT, 0x6897BB);
		setTokenFgColor(SyntaxScheme.LITERAL_NUMBER_HEXADECIMAL, 0x6897BB);
		setTokenFgColor(SyntaxScheme.LITERAL_NUMBER_FLOAT, 0x6897BB);
		setTokenFgColor(SyntaxScheme.MARKUP_TAG_DELIMITER, 0xCCCCCC);
		setTokenFgColor(SyntaxScheme.MARKUP_TAG_NAME, 0x99FF99);
		setTokenFgColor(SyntaxScheme.MARKUP_TAG_ATTRIBUTE, 0xCCCCCC);
		setTokenFgColor(SyntaxScheme.MARKUP_TAG_ATTRIBUTE_VALUE, 0x00CCCC);
		setTokenFgColor(SyntaxScheme.MARKUP_COMMENT, colorComment);
		setTokenFgColor(SyntaxScheme.MARKUP_ENTITY_REFERENCE, 0xCCCCCC);
		setTokenFgColor(SyntaxScheme.OPERATOR, 0xE6E6FA);
		setTokenFgColor(SyntaxScheme.FUNCTION, 0xA27802);

		myScheme.getStyle(TokenTypes.SEPARATOR).foreground = new Color(0xFFD40B);

		textArea.setSyntaxScheme(myScheme);

		if (scrollPane.getGutter() != null)
		{
			scrollPane.getGutter().setBackground(gutterBg);
			scrollPane.getGutter().setBorderColor(border);
			scrollPane.getGutter().setLineNumberColor(fg);
		}

	}

	private void setTokenFgColor(int aTokenType, int aColor)
	{

		Style s = myScheme.getStyle(aTokenType);
		s.foreground = new Color(aColor);
		myScheme.setStyle(aTokenType, s);

	}

	@Override
	public void searchEvent(SearchEvent e)
	{
		SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();
		SearchResult result;

		switch (type)
		{
			default: // Prevent FindBugs warning later
			case MARK_ALL:
				result = SearchEngine.markAll(textArea, context);
				break;
			case FIND:
				result = SearchEngine.find(textArea, context);
				if (!result.wasFound() || result.isWrapped())
				{
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				}
				break;
			case REPLACE:
				result = SearchEngine.replace(textArea, context);
				if (!result.wasFound() || result.isWrapped())
				{
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				}
				break;
			case REPLACE_ALL:
				result = SearchEngine.replaceAll(textArea, context);
				JOptionPane.showMessageDialog(null, result.getCount() + " occurrences replaced.");
				break;
		}

		String text;
		if (result.wasFound())
		{
			text = "Text found; occurrences marked: " + result.getMarkedCount();
		}
		else if (type == SearchEvent.Type.MARK_ALL)
		{
			if (result.getMarkedCount() > 0)
			{
				text = "Occurrences marked: " + result.getMarkedCount();
			}
			else
			{
				text = "";
			}
		}
		else
		{
			text = "Text not found";
		}

		// statusBar.setLabel(text);

	}

	@Override
	public String getSelectedText()
	{
		return textArea.getSelectedText();
	}

	/**
	 * Opens the "Go to Line" dialog.
	 */
	private class GoToLineAction extends AbstractAction
	{

		GoToLineAction()
		{
			super("Go To Line...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (findDialog.isVisible())
			{
				findDialog.setVisible(false);
			}
			if (replaceDialog.isVisible())
			{
				replaceDialog.setVisible(false);
			}
			GoToDialog dialog = new GoToDialog(XmlEditor.this);
			dialog.setMaxLineNumberAllowed(textArea.getLineCount());
			dialog.setVisible(true);
			int line = dialog.getLineNumber();
			if (line > 0)
			{
				try
				{
					textArea.setCaretPosition(textArea.getLineStartOffset(line - 1));
				}
				catch(BadLocationException ble)
				{ // Never happens
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
					ble.printStackTrace();
				}
			}
		}

	}

	/**
	 * Shows the Find dialog.
	 */
	private class ShowFindDialogAction extends AbstractAction
	{

		ShowFindDialogAction()
		{
			super("Find...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (replaceDialog.isVisible())
			{
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}

	/**
	 * Shows the Replace dialog.
	 */
	private class ShowReplaceDialogAction extends AbstractAction
	{

		ShowReplaceDialogAction()
		{
			super("Replace...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, c));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (findDialog.isVisible())
			{
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
		}

	}

	/**
	 * The status bar for this application.
	 */
	private static class StatusBar extends JPanel
	{

		private JLabel label;

		StatusBar()
		{
			label = new JLabel("Ready");
			setLayout(new BorderLayout());
			add(label, BorderLayout.LINE_START);
			add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
		}

		void setLabel(String label)
		{
			this.label.setText(label);
		}

	}

}