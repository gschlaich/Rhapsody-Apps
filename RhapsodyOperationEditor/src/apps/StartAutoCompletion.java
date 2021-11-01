package apps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rsyntaxtextarea.parser.TaskTagParser;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;

import RhapsodyUtilities.RhapsodyOperation;
import apps.ClassifierCompletionProvider.visibility;
import parser.CppParser;
import parser.DiffParser;
import parser.TypeParser;

public class StartAutoCompletion extends Thread
{
	private RSyntaxTextArea myTextArea;
	private AutoCompletion myAutoComplete;
	private IRPOperation mySelectedOperation;
	private ClassifierCompletionProvider myClassifierCompletionProvider;
	private IRPApplication myApplication;
	
	private static StartAutoCompletion instance;
	
	public StartAutoCompletion(
			RSyntaxTextArea aTextArea, 
			AutoCompletion aAutoComplete, 
			IRPOperation aSelectedOperation,
			IRPApplication aApplication) 
	{
		myTextArea = aTextArea;
		//myAutoComplete = aAutoComplete;
		mySelectedOperation = aSelectedOperation;
		myApplication = aApplication;
	}
	
	
	
	public static ClassifierCompletionProvider GetClassifierCompletionProvider()
	{
		if(instance!=null)
		{
			return instance.myClassifierCompletionProvider;
		}
		
		return null;
	}
	
	
	
	public void run()
	{
		startAutoComplete();
	}
	
	public void startAutoComplete()
	{
		
		instance = this;
		
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		
	    
		myApplication.writeToOutputWindow("log", "Start Autocomplete collection at " + timeStamp + "\n");
		CompletionProvider provider = createCompletionProvider(mySelectedOperation);
		// Install auto-completion onto our text area.
		myAutoComplete = new AutoCompletion(provider);
		myAutoComplete.setListCellRenderer(new CPPCellRenderer());
		myAutoComplete.setShowDescWindow(true);
		myAutoComplete.setParameterAssistanceEnabled(true);
		myAutoComplete.setAutoCompleteEnabled(true);
		myAutoComplete.setAutoActivationEnabled(true);
		myAutoComplete.setAutoActivationDelay(750);
		myAutoComplete.install(myTextArea);
		timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		myApplication.writeToOutputWindow("log", "Complete Autocomplete collection at " + timeStamp + "\n" );
		
		
		

		JPopupMenu popup = myTextArea.getPopupMenu();
	    popup.addSeparator();
	    popup.add(new JMenuItem(new OpenFeature(myClassifierCompletionProvider)));
	    popup.add(new JMenuItem(new AddDependency(mySelectedOperation)));
	    
	    FoldParserManager.get().addFoldParserMapping("text/RhapsodyCPP", new CurlyFoldParser());
	   
	    
	    myTextArea.setSyntaxEditingStyle("text/RhapsodyCPP");
	
		myTextArea.setCodeFoldingEnabled(true);
		
		TaskTagParser taskTagParser = new TaskTagParser();
		myTextArea.addParser(taskTagParser);
		
		
		
		Gutter gutter = RSyntaxUtilities.getGutter(myTextArea);
		
		
		gutter.setBookmarkIcon(RhapsodyOperation.getIcon("RhapsodyIcons_110.gif")); 
		gutter.setBookmarkingEnabled(true); 
		
		
		 //start parser
	    CppParser parser = new CppParser(myClassifierCompletionProvider, gutter);
	    myTextArea.addParser(parser);
		
		
		DiffParser diffParser = new DiffParser(myTextArea.getText(),gutter);
		myTextArea.addParser(diffParser);
		
		
		
		
		
	
		
		
	}
	
	
	/**
	 * Creates the completion provider for a C editor.  This provider can be
	 * shared among multiple editors.
	 * @param aClass 
	 *
	 * @return The provider.
	 */
	private CompletionProvider createCompletionProvider(IRPOperation selectedOperation) {

		IRPClass selectedClass = (IRPClass) selectedOperation.getOwner();
		
		// Create the provider used when typing code.
		myClassifierCompletionProvider = new ClassifierCompletionProvider(selectedClass, visibility.v_private);
		//LocalCompletionProvider localCP = new LocalCompletionProvider(selectedOperation.getBody());
		
		
		@SuppressWarnings("unchecked")
		List<IRPArgument> arguments = selectedOperation.getArguments().toList();
		for(IRPArgument argument : arguments)
		{
			RhapsodyArgumentCompletion rac = new RhapsodyArgumentCompletion(myClassifierCompletionProvider, argument);
			myClassifierCompletionProvider.addCompletion(rac);
		}
		
		createTraceCompletions(myClassifierCompletionProvider);
		
		getNameSpaces(myClassifierCompletionProvider, selectedOperation);
		
		// The provider used when typing a string.
		CompletionProvider stringCP = createStringCompletionProvider();

		// The provider used when typing a comment.
		CompletionProvider commentCP = createCommentCompletionProvider();

		// Create the "parent" completion provider.
		LanguageAwareCompletionProvider provider = new LanguageAwareCompletionProvider(myClassifierCompletionProvider);
		provider.setStringCompletionProvider(stringCP);
		provider.setCommentCompletionProvider(commentCP);


		return provider;

	}
	
	/**
	 * Returns the completion provider to use when the caret is in a string.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createCommentCompletionProvider()
	 */
	private CompletionProvider createStringCompletionProvider() {
		DefaultCompletionProvider cp = new SourceCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "%c", "char", "Prints a character"));
		cp.addCompletion(new BasicCompletion(cp, "%i", "signed int", "Prints a signed integer"));
		cp.addCompletion(new BasicCompletion(cp, "%f", "float", "Prints a float"));
		cp.addCompletion(new BasicCompletion(cp, "%s", "string", "Prints a string"));
		cp.addCompletion(new BasicCompletion(cp, "%u", "unsigned int", "Prints an unsigned integer"));
		cp.addCompletion(new BasicCompletion(cp, "%d", "int", "Prints a decimal value"));
		cp.addCompletion(new BasicCompletion(cp, "\\n", "Newline", "Prints a newline"));
		return cp;
	}
	
	/**
	 * Returns the provider to use when in a comment.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createStringCompletionProvider()
	 */
	private CompletionProvider createCommentCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
		cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
		cp.addCompletion(new BasicCompletion(cp, "Jira Issue:", "See USM-XXX "));
		return cp;
	}
	
	private void createTraceCompletions(ClassifierCompletionProvider aCp)
	{
		
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_RELEASE3"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_RELEASE(X_ERROR)", "Error Release Trace in USMMonitor and log file"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_RELEASE(X_WARNING)", "Warning Release Trace in USMMonitor and log file"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_RELEASE(X_INFO)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_DEBUG(X_ERROR)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_DEBUG(X_WARNING)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_DEBUG(X_INFO)"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_DEBUG"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_DEBUG3"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_TRACE_RELEASE"));	
		aCp.addCompletion(new BasicCompletion(aCp, "X_ASSERT"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_INFO"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_WARNING"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_ERROR"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_FATAL"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_DEFAULT"));
		aCp.addCompletion(new BasicCompletion(aCp, "nullptr", "defined value when pointer is null"));
		aCp.addCompletion(new BasicCompletion(aCp, "static_cast", "static cast of data type"));
		aCp.addCompletion(new BasicCompletion(aCp, "dynamic_cast", "dynamic cast of data type"));
		aCp.addCompletion(new BasicCompletion(aCp, "const_cast", "const cast of data type"));
	

	}
	
	private void getNameSpaces(DefaultCompletionProvider provider, IRPModelElement selected) {
		IRPModelElement e =  selected;
	    
	    while((e instanceof IRPPackage)==false)
	    {
	    	e = e.getOwner();
	    }
	    
	    //System.out.println(e.getPropertyValue("CPP_CG.Package.DefineNameSpace"));
	    while(e.getPropertyValue("CPP_CG.Package.DefineNameSpace").equals("False"))
	    {
	    	e = e.getOwner();
	    }
	    
	    if (e instanceof IRPPackage)
    	{
    		IRPPackage p = (IRPPackage)e;
    		
    		RhapsodyNamespaceCompletion rnc = new RhapsodyNamespaceCompletion(provider, p);
    		provider.addCompletion(rnc);
    		
    		
    		//System.out.println(e.getName());
    		List<IRPDependency> dependencies = p.getDependencies().toList();
    		for(IRPDependency dependency: dependencies)
    		{
    			if(dependency.getDependsOn() instanceof IRPPackage)
    			{
    				p = (IRPPackage) dependency.getDependsOn();
    				rnc = new RhapsodyNamespaceCompletion(provider, p); 
    				provider.addCompletion(rnc);
    			}
    				
    		}    		
    	}
	}


	
	
}

