package de.schlaich.gunnar.rhapsody.utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;

/**
 * Dialog für die Anzeige von Suchergebnissen aus dem Rhapsody-Modell.
 * Zeigt eine Tabelle mit Name, Element Type und vollständigem Pfad an.
 */
public class CSearchResult extends JDialog
{

	private static final long serialVersionUID = 1L;

	private IRPApplication myRhapsody;
	private final List<IRPModelElement> myElements = new ArrayList<>();
	private final List<String> myFields = new ArrayList<>();
	private DefaultTableModel myTableModel;
	private JTable myTable;
	private JLabel myStatusLabel;
	private JButton myLocateButton;
	private JButton myFeaturesButton;
	private JButton myCloseButton;

	public CSearchResult(IRPApplication aRhapsody)
	{
		this(null, aRhapsody, (String) null);
	}

	public CSearchResult(Window owner, IRPApplication aRhapsody)
	{
		this(owner, aRhapsody, (String) null);
	}

	public CSearchResult(Window owner, IRPApplication aRhapsody, String aSearchText)
	{
		super(owner, getDialogTitle(aSearchText), Dialog.ModalityType.MODELESS);
		this.myRhapsody = aRhapsody;
		initUI();
	}

	public CSearchResult(Window owner, IRPApplication aRhapsody, List<IRPModelElement> aElements)
	{
		this(owner, aRhapsody, (String) null);
		if (aElements != null)
		{
			for (IRPModelElement elem : aElements)
			{
				addElement(elem);
			}
		}
	}

	private static String getDialogTitle(String aSearchText)
	{
		if (aSearchText != null && !aSearchText.trim().isEmpty())
		{
			return "Search Results - " + aSearchText.trim();
		}
		return "Search Results";
	}

	public void updateTitle(String aSearchText)
	{
		setTitle(getDialogTitle(aSearchText));
	}

	private void initUI()
	{
		String[] columnNames = { "Name", "Element Type", "Found in Field", "Full Path" };

		myTableModel = new DefaultTableModel(columnNames, 0)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		myTable = new JTable(myTableModel);
		myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		myTable.setAutoCreateRowSorter(true);
		myTable.setFillsViewportHeight(true);
		myTable.setRowHeight(22);

		// Custom renderer for the first column to display icon next to name
		myTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setIcon(null);
				if (column == 0 && row >= 0 && row < table.getRowCount())
				{
					int modelRow = table.convertRowIndexToModel(row);
					if (modelRow >= 0 && modelRow < myElements.size())
					{
						IRPModelElement element = myElements.get(modelRow);
						if (element != null)
						{
							try
							{
								String iconPath = element.getIconFileName();
								if (iconPath != null && !iconPath.isEmpty() && new File(iconPath).exists())
								{
									setIcon(new ImageIcon(iconPath));
								}
							}
							catch (Exception ignored)
							{
							}
						}
					}
				}
				return c;
			}
		});

		TableColumnModel colModel = myTable.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(150);
		colModel.getColumn(1).setPreferredWidth(100);
		colModel.getColumn(2).setPreferredWidth(120);
		colModel.getColumn(3).setPreferredWidth(300);

		myTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					locateSelectedElement();
				}
			}
		});

		myTable.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					locateSelectedElement();
					e.consume();
				}
			}
		});

		myTable.getSelectionModel().addListSelectionListener(e ->
		{
			boolean hasSelection = myTable.getSelectedRow() >= 0;
			if (myLocateButton != null)
			{
				myLocateButton.setEnabled(hasSelection);
			}
			if (myFeaturesButton != null)
			{
				myFeaturesButton.setEnabled(hasSelection);
			}
		});

		JScrollPane scrollPane = new JScrollPane(myTable);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		myLocateButton = new JButton("Locate in Browser");
		myLocateButton.setEnabled(false);
		myLocateButton.addActionListener(e -> locateSelectedElement());

		myFeaturesButton = new JButton("Open Features");
		myFeaturesButton.setEnabled(false);
		myFeaturesButton.addActionListener(e -> openFeaturesSelectedElement());

		myCloseButton = new JButton("Close");
		myCloseButton.addActionListener(e -> dispose());

		buttonPanel.add(myLocateButton);
		buttonPanel.add(myFeaturesButton);
		buttonPanel.add(myCloseButton);

		myStatusLabel = new JLabel(" Ready");
		myStatusLabel.setBorder(new EmptyBorder(4, 8, 4, 8));

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(myStatusLabel, BorderLayout.WEST);
		bottomPanel.add(buttonPanel, BorderLayout.EAST);

		JPanel content = new JPanel(new BorderLayout());
		content.add(scrollPane, BorderLayout.CENTER);
		content.add(bottomPanel, BorderLayout.SOUTH);

		setContentPane(content);

		if (myRhapsody != null)
		{
			try
			{
				IRPProject project = myRhapsody.activeProject();
				if (project != null)
				{
					String iconFile = project.getIconFileName();
					if (iconFile != null && new File(iconFile).exists())
					{
						setIconImage(new ImageIcon(iconFile).getImage());
					}
				}
			}
			catch (Exception ignored)
			{
			}
		}

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// Wichtig: pack() MUSS VOR setLocationRelativeTo aufgerufen werden
		pack();
		// Aber mit minimalen Dimensionen, um zu großen Dialog zu vermeiden
		if (getWidth() < 750)
		{
			setSize(750, 450);
		}
		
		// Zentriere das Dialog relativ zum Owner, wenn vorhanden
		Window owner = getOwner();
		if (owner != null && owner.isVisible())
		{
			setLocationRelativeTo(owner);
		}
		else
		{
			setLocationByPlatform(true);
		}
	}

	public void addElement(IRPModelElement aElement)
	{
		addElement(aElement, "");
	}

	public void addElement(IRPModelElement aElement, String aField)
	{
		if (aElement == null)
		{
			return;
		}

		//Runnable task = () ->
		//{
			myElements.add(aElement);
			myFields.add(aField != null ? aField : "");
			String name = "";
			String metaClass = "";
			String path = "";
			try
			{
				name = aElement.getName();
			}
			catch (Exception e)
			{
				name = aElement.toString();
			}
			try
			{
				metaClass = aElement.getMetaClass();
			}
			catch (Exception e)
			{
				metaClass = "";
			}
			try
			{
				path = aElement.getFullPathName();
			}
			catch (Exception e)
			{
				path = "";
			}
			myTableModel.addRow(new Object[] { name, metaClass, aField != null ? aField : "", path });
			myStatusLabel.setText(" " + myElements.size() + " element(s) found");
			
			// Stelle sicher, dass die Tabelle aktualisiert wird
			myTable.revalidate();
			myTable.repaint();
			return;
		//};

//		if (SwingUtilities.isEventDispatchThread())
//		{
//			task.run();
//		}
//		else
//		{
//			SwingUtilities.invokeLater(task);
//		}
	}

	public void addElements(List<IRPModelElement> aElements)
	{
		if (aElements == null)
		{
			return;
		}
		for (IRPModelElement element : aElements)
		{
			addElement(element);
		}
	}

	public void setElements(List<IRPModelElement> aElements)
	{
		clear();
		addElements(aElements);
	}

	public List<IRPModelElement> getElements()
	{
		return new ArrayList<>(myElements);
	}

	public IRPModelElement getSelectedElement()
	{
		int selectedRow = myTable.getSelectedRow();
		if (selectedRow >= 0)
		{
			int modelRow = myTable.convertRowIndexToModel(selectedRow);
			if (modelRow >= 0 && modelRow < myElements.size())
			{
				return myElements.get(modelRow);
			}
		}
		return null;
	}

	public void clear()
	{
		Runnable task = () ->
		{
			myElements.clear();
			myFields.clear();
			myTableModel.setRowCount(0);
			myStatusLabel.setText(" Ready");
		};

		if (SwingUtilities.isEventDispatchThread())
		{
			task.run();
		}
		else
		{
			SwingUtilities.invokeLater(task);
		}
	}

	public void searchStarted()
	{
		Runnable task = () ->
		{
			clear();
			myStatusLabel.setText(" Searching...");
		};

		if (SwingUtilities.isEventDispatchThread())
		{
			task.run();
		}
		else
		{
			SwingUtilities.invokeLater(task);
		}
	}

	public void searchEnded()
	{
		Runnable task = () ->
		{
			myStatusLabel.setText(" Search finished. " + myElements.size() + " element(s) found.");
			// Stelle sicher, dass das Dialog sichtbar und im Vordergrund ist
			if (!isVisible())
			{
				setVisible(true);
			}
			toFront();
			repaint();
		};

		if (SwingUtilities.isEventDispatchThread())
		{
			task.run();
		}
		else
		{
			SwingUtilities.invokeLater(task);
		}
	}

	private void locateSelectedElement()
	{
		int selectedRow = myTable.getSelectedRow();
		if (selectedRow >= 0)
		{
			int modelRow = myTable.convertRowIndexToModel(selectedRow);
			if (modelRow >= 0 && modelRow < myElements.size())
			{
				IRPModelElement element = myElements.get(modelRow);
				if (element != null)
				{
					try
					{
						element.locateInBrowser();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}

	private void openFeaturesSelectedElement()
	{
		int selectedRow = myTable.getSelectedRow();
		if (selectedRow >= 0)
		{
			int modelRow = myTable.convertRowIndexToModel(selectedRow);
			if (modelRow >= 0 && modelRow < myElements.size())
			{
				IRPModelElement element = myElements.get(modelRow);
				if (element != null)
				{
					try
					{
						element.openFeaturesDialog(0);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
