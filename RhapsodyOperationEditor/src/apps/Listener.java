package apps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class Listener implements CaretListener, ActionListener {

	
	private RSyntaxTextArea textArea;
	private Timer t;

	public Listener(RSyntaxTextArea textArea) {
		this.textArea = textArea;
		textArea.addCaretListener(this);
		t = new Timer(650, this);
		t.setRepeats(false);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caretUpdate(CaretEvent e) {
		// TODO Auto-generated method stub

	}

}
