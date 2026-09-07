package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.telelogic.rhapsody.core.IRPHyperLink;

public class XmlEditor extends JDialog
{
    private final RSyntaxTextArea textArea = new RSyntaxTextArea();
    private final File xmlFile;
    private boolean okPressed = false;

    public XmlEditor(Window owner, File xmlFile)
    {
        super(owner, xmlFile == null ? "Edit XML" : "Edit XML - " + xmlFile.getName(), Dialog.ModalityType.APPLICATION_MODAL);
        this.xmlFile = xmlFile;
        buildUI();
        setMinimumSize(new Dimension(700, 500));
        pack();
        setLocationRelativeTo(owner);
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
        Thread editorThread = new Thread(() -> {
            try
            {
                SwingUtilities.invokeAndWait(() -> {
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
            catch (Exception ex)
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
        Thread editorThread = new Thread(() -> {
            try
            {
                SwingUtilities.invokeAndWait(() -> {
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
            catch (Exception ex)
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

    public static boolean openHyperLink(IRPHyperLink link)
    {
        if (link == null)
        {
            return false;
        }

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

        showDialogAsync(null, xmlFile, text ->
        {
            if (text == null)
            {
                return;
            }

            try
            {
                Files.write(xmlFile.toPath(), text.getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        return true;
    }

    private void buildUI()
    {
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setLineWrap(false);

        if (xmlFile != null && xmlFile.exists())
        {
            try
            {
                byte[] bytes = Files.readAllBytes(xmlFile.toPath());
                textArea.setText(new String(bytes, StandardCharsets.UTF_8));
            }
            catch (IOException e)
            {
                textArea.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<!-- error reading file -->\n");
            }
        }

        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setFoldIndicatorEnabled(true);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            okPressed = true;
            if (xmlFile != null)
            {
                try
                {
                    Files.write(xmlFile.toPath(), textArea.getText().getBytes(StandardCharsets.UTF_8));
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonBar.add(cancelButton);
        buttonBar.add(okButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(scrollPane), BorderLayout.CENTER);
        getContentPane().add(buttonBar, BorderLayout.SOUTH);
    }
}