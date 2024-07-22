package de.schlaich.gunnar.rhapsody.operationeditor;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.AutoCompletionEvent;
import org.fife.ui.autocomplete.AutoCompletionListener;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.DescWindowCallback;
import org.fife.ui.autocomplete.ExternalURLHandler;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rsyntaxtextarea.parser.TaskTagParser;
import org.fife.ui.rtextarea.Gutter;

import com.github.difflib.algorithm.myers.MyersDiff;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPHyperLink;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.parser.CodeAnalysisParser;
import de.schlaich.gunnar.parser.CppParser;
import de.schlaich.gunnar.parser.DiffParser;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyArgumentCompletion;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyClassifier;
import de.schlaich.gunnar.rhapsody.completion.RhapsodyNamespaceCompletion;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider.visibility;
import de.schlaich.gunnar.rhapsody.completionprovider.LocalCompletionProvider;
import de.schlaich.gunnar.rhapsody.completionprovider.NamespaceCompletionProvider;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class StartAutoCompletion extends Thread
{
	private RSyntaxTextArea myTextArea;
	private AutoCompletion myAutoComplete;
	private IRPOperation mySelectedOperation;
	private ClassifierCompletionProvider myClassifierCompletionProvider;
	private LocalCompletionProvider myLocalCompletionProvider = null;
	private IRPApplication myApplication;
	private DiffParser myDiffParser;
	private CppParser myCppParser;
	private CodeAnalysisParser myCodeAnalysisParser;
	private JFrame myFrame;
	
	private static StartAutoCompletion instance;
	
	public StartAutoCompletion(
			RSyntaxTextArea aTextArea, 
			AutoCompletion aAutoComplete, 
			IRPOperation aSelectedOperation,
			IRPApplication aApplication,
			JFrame aFrame) 
	{
		myTextArea = aTextArea;
		//myAutoComplete = aAutoComplete;
		mySelectedOperation = aSelectedOperation;
		myApplication = aApplication;
		myFrame = aFrame;
	}
	
	
	
	public static ClassifierCompletionProvider GetClassifierCompletionProvider()
	{
		if(instance!=null)
		{
			return instance.myClassifierCompletionProvider;
		}
		
		return null;
	}
	
	
	public static LocalCompletionProvider GetLocalCompletionProvider()
	{
		if(instance!=null)
		{
			return instance.myLocalCompletionProvider;
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
		
		Date started = new Date();
		//String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		
		
		myApplication.writeToOutputWindow("Log", RhapsodyOperation.getOperation(mySelectedOperation));
		myApplication.writeToOutputWindow("Log", " in " + mySelectedOperation.getOwner().getOwner().getFullPathName()+"\n\n");
		
		
	    
		myApplication.writeToOutputWindow("log", "Start Autocomplete collection \n");
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
		
		myAutoComplete.setAutoCompleteSingleChoices(false);
		myAutoComplete.addAutoCompletionListener(new AutoCompletionListener() {
			
			@Override
			public void autoCompleteUpdate(AutoCompletionEvent e) 
			{
				System.out.println("AutoCompletionUpdate " + e.toString());
				
			}
		});
		
		
		
		myAutoComplete.setExternalURLHandler(new ExternalURLHandler() {
			
			@Override
			public void urlClicked(HyperlinkEvent e, Completion c, DescWindowCallback callback) {
				
				System.out.println(c.getSummary());
				IRPProject project = myApplication.activeProject();
				
				System.out.println(mySelectedOperation.getToolTipHTML());
				
				
				if(project==null)
				{
					return;
				}
				
				List<IRPHyperLink> links = mySelectedOperation.getHyperLinks().toList();
				
				for(IRPHyperLink link:links)
				{
					System.out.println(link.getURL());
				}
				
				
				
				/*
				project.view
				
				
				
				
				String elementName = c.toString();
				
				
				if(c instanceof RhapsodyClassifier)
				{
					RhapsodyClassifier rc = (RhapsodyClassifier)c;
					
					IRPModelElement element = rc.getElement();
					if(element==null)
					{
						return;
					}
					element.openFeaturesDialog(1);
				}
				
				*/
			
				
				/*
				System.out.println(elementName);
				
				IRPProject project = myApplication.activeProject();
				
				
				if(project==null)
				{
					return;
				}
				
				
				
				
				IRPModelElement clickedElement = project.fin
				
				if(clickedElement==null)
				{
					System.out.println(guid + " is not a GUID?!");
					return;
				}
				
				clickedElement.openFeaturesDialog(1);
				*/


				
			}
		});
		

	

		JPopupMenu popup = myTextArea.getPopupMenu();

	    popup.addSeparator();
	    popup.add(new JMenuItem(new GetInfo(myAutoComplete)));
	    popup.add(new JMenuItem(new SearchElement(myClassifierCompletionProvider,myApplication)));
	    popup.add(new JMenuItem(new OpenFeature(myClassifierCompletionProvider, myApplication)));
	    popup.add(new JMenuItem(new AddDependency(mySelectedOperation)));
	    popup.add(new JMenuItem(new SetBreakpoint(mySelectedOperation, myTextArea)));
	    popup.addSeparator();
	    //popup.add(new JMenuItem(new AskGPtIssue(myApplication)));
	    //popup.add(new JMenuItem(new AskGPtOptimize(myApplication)));
	    
	    
	    FoldParserManager.get().addFoldParserMapping("text/RhapsodyCPP", new CurlyFoldParser());
	   
	    
	    myTextArea.setSyntaxEditingStyle("text/RhapsodyCPP");
		myTextArea.setCodeFoldingEnabled(true);
		
		 //start parser
		Gutter gutter = RSyntaxUtilities.getGutter(myTextArea);

		gutter.setBookmarkIcon(RhapsodyOperation.getIcon("RhapsodyIcons_110.gif")); 
		gutter.setBookmarkingEnabled(true); 
		
	    myCppParser = new CppParser(myClassifierCompletionProvider, gutter);
	    myTextArea.addParser(myCppParser);
		myDiffParser = new DiffParser(myTextArea.getText(),gutter);
		myTextArea.addParser(myDiffParser);
		myCodeAnalysisParser = new CodeAnalysisParser(mySelectedOperation, gutter);
		myTextArea.addParser(myCodeAnalysisParser);
		
		
		
		myClassifierCompletionProvider.createClassCompletion();
		myTextArea.forceReparsing(myCppParser);
		myTextArea.forceReparsing(myCodeAnalysisParser);
		
		myLocalCompletionProvider = new LocalCompletionProvider(myTextArea.getText(), myClassifierCompletionProvider);
		
		
		
		
		//timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		
		Date finished = new Date();
		
		double diffMs = finished.getTime()-started.getTime();
		
		double diffS = diffMs/1000; 
	
		
		myApplication.writeToOutputWindow("log", "Complete Autocomplete collection after " + diffS + " Seconds\n\n\n" );
			
	}
	
	public void updateCompletionProvider()
	{
		Date started = new Date();
		
		myFrame.setTitle(RhapsodyOperation.getOperation(mySelectedOperation));
		
		
		myClassifierCompletionProvider.clear();
		myApplication.writeToOutputWindow("log", "Start Autocomplete update \n");
		CompletionProvider provider = createCompletionProvider(mySelectedOperation);
		myAutoComplete.setCompletionProvider(provider);
		myClassifierCompletionProvider.createClassCompletion();
				
		Gutter gutter = RSyntaxUtilities.getGutter(myTextArea);
		
		myCppParser = new CppParser(myClassifierCompletionProvider, gutter);
	    myTextArea.addParser(myCppParser);
		myDiffParser = new DiffParser(myTextArea.getText(),gutter);
		myTextArea.addParser(myDiffParser);
		myCodeAnalysisParser = new CodeAnalysisParser(mySelectedOperation, gutter);
		myTextArea.addParser(myCodeAnalysisParser);
		
		
		myClassifierCompletionProvider.resetClassCompletionCreated();
		
		getNameSpaces(myClassifierCompletionProvider, mySelectedOperation);

		@SuppressWarnings("unchecked")
		List<IRPArgument> arguments = mySelectedOperation.getArguments().toList();
		for(IRPArgument argument : arguments)
		{
			RhapsodyArgumentCompletion rac = new RhapsodyArgumentCompletion(myClassifierCompletionProvider, argument);
			myClassifierCompletionProvider.addCompletion(rac);
		}
		
		myClassifierCompletionProvider.createClassCompletion();
		myTextArea.forceReparsing(myCppParser);
		myTextArea.forceReparsing(myCodeAnalysisParser);
				
		Date finished = new Date();
		double diffMs = finished.getTime()-started.getTime();
		double diffS = diffMs/1000; 
		myApplication.writeToOutputWindow("log", "Complete Autocomplete update after " + diffS + " Seconds\n\n\n" );
			
	}
	
	
	
	public DiffParser getDiffParser()
	{
		return myDiffParser;
	}
	
	public CodeAnalysisParser getCodeAnalysisParser()
	{
		return myCodeAnalysisParser;
	}
	
	public void forceCodeAnalysisParser()
	{
		myTextArea.forceReparsing(myCodeAnalysisParser);
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
		myClassifierCompletionProvider = ClassifierCompletionProvider.GetProvider(selectedClass, visibility.v_private,true);
		//LocalCompletionProvider localCP = new LocalCompletionProvider(selectedOperation.getBody());
	
		getNameSpaces(myClassifierCompletionProvider, selectedOperation);
		
		
		
		@SuppressWarnings("unchecked")
		List<IRPArgument> arguments = selectedOperation.getArguments().toList();
		for(IRPArgument argument : arguments)
		{
			RhapsodyArgumentCompletion rac = new RhapsodyArgumentCompletion(myClassifierCompletionProvider, argument);
			myClassifierCompletionProvider.addCompletion(rac);
		}
		
		
		
		createTraceCompletions(myClassifierCompletionProvider);
		
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
	
		
		List<Parameter> xtparams = new ArrayList<Parameter>();
		Parameter level = new Parameter("TCSI::CTraceEndpoint::ETraceLevel", "Level");
		level.setDescription("set Trace level:\nX_FATAL\nX_ERROR\nX_WARNING\nX_INFO\nX_LOG\nX_DEBUG");
		xtparams.add(level);
		
		FunctionCompletion xtc = new FunctionCompletion(aCp, "X_TRACE_RELEASE", "");
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("Trace to USM Monitor or log file in Debug and Release version");
		
		xtc.setParams(xtparams);
		aCp.addCompletion(xtc);
		
	
		xtc = new FunctionCompletion(aCp, "X_TRACE_DEBUG", "");	
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("Trace to USM Monitor or log file only in Debug version");
		xtc.setParams(xtparams);
		aCp.addCompletion(xtc);
		
		List<Parameter> xtimeparams = new ArrayList<Parameter>();
		xtimeparams.add(level);
		Parameter text = new Parameter("const char*","Text");
		text.setDescription("Name of the time Measure");
		xtimeparams.add(text);
		
		xtc = new FunctionCompletion(aCp, "X_TIME_RELEASE", "");	
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("Measure time to closing curly bracket (from constructor to destructor)");
		xtc.setParams(xtimeparams);
		aCp.addCompletion(xtc);
		
		xtc = new FunctionCompletion(aCp, "X_TIME_DEBUG", "");	
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("Measure time to closing curly bracket (from constructor to destructor)");
		xtc.setParams(xtimeparams);
		aCp.addCompletion(xtc);
		
		List<Parameter> xlogparams = new ArrayList<Parameter>();
		text = new Parameter("const char*","Filename");
		text.setDescription("Filename where to write"
				+ "");
		xlogparams.add(text);	
		
		xtc = new FunctionCompletion(aCp, "X_LOG_FILE_DEBUG", "");	
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("TraceEndpoint to log file");
		xtc.setParams(xlogparams);
		aCp.addCompletion(xtc);
		
		xtc = new FunctionCompletion(aCp, "X_LOG_FILE_RELEASE", "");	
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("TraceEndpoint to log file");
		xtc.setParams(xlogparams);
		aCp.addCompletion(xtc);
		
		Parameter type = new Parameter("TCSI::CTraceEndpoint::ETraceType","Type");
		type.setDescription("set Trace type:\nX_DEFAULT\nX_IN\nX_OUT\nX_PROC\nX_MASTER\nX_SLAVE\nX_MANUAL\nX_HARDWARE");
		xtparams.add(type);
		
		xtc = new FunctionCompletion(aCp, "X_TRACE_RELEASE2", "");	
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("Trace to USM Monitor or log file in Debug and Release version");
		xtc.setParams(xtparams);
		aCp.addCompletion(xtc);
		
		xtc = new FunctionCompletion(aCp, "X_TRACE_DEBUG2", "");	
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("Trace to USM Monitor or log file only in Debug version");
		xtc.setParams(xtparams);
		aCp.addCompletion(xtc);
		
		Parameter endpoint = new Parameter("TCSI::CTraceEndpoint","endpoint");
		endpoint.setDescription("set Endpoint");
		xtparams.add(0, endpoint);
		
		xtc = new FunctionCompletion(aCp, "X_TRACE_RELEASE3", "");	
		xtc.setDefinedIn("TCSI::CTraceEndpoint");
		xtc.setShortDescription("Trace to USM Monitor or log file in Debug and Release version");
		xtc.setParams(xtparams);
		aCp.addCompletion(xtc);

		
		xtc = new FunctionCompletion(aCp, "X_ASSERT", "");
		List<Parameter> assertParams = new ArrayList<Parameter>();
		Parameter assertParam = new Parameter("bool","isValid");
		assertParam.setDescription("Throw assertion when false");
		assertParams.add(assertParam);
		xtc.setParams(assertParams);
		xtc.setDefinedIn("");
		aCp.addCompletion(xtc);
		
		xtc = new FunctionCompletion(aCp, "X_VERIFY", "");
		assertParam.setDescription("Assert on DEBUG when false");
		xtc.setParams(assertParams);
		xtc.setDefinedIn("");
		aCp.addCompletion(xtc);
		
		aCp.addCompletion(new BasicCompletion(aCp, "X_INFO","Trace Level define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_WARNING","Trace Level define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_ERROR","Trace Level define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_FATAL","Trace Level define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_DEBUG","Trace Level define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_LOG","Trace Level define"));
		
		aCp.addCompletion(new BasicCompletion(aCp, "X_DEFAULT","Trace Type define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_OUT","Trace Type define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_PROC","Trace Type define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_MASTER","Trace Type define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_SLAVE","Trace Type define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_MANUAL","Trace Type define"));
		aCp.addCompletion(new BasicCompletion(aCp, "X_HARDWARE","Trace Type define"));
		
		
		aCp.addCompletion(new BasicCompletion(aCp, "nullptr", "defined value when pointer is null"));
		aCp.addCompletion(new BasicCompletion(aCp, "static_cast", "static cast of data type"));
		aCp.addCompletion(new BasicCompletion(aCp, "dynamic_cast", "dynamic cast of data type"));
		aCp.addCompletion(new BasicCompletion(aCp, "const_cast", "const cast of data type"));
		aCp.addCompletion(new BasicCompletion(aCp, "reinterpret_cast", "Converts between types by reinterpreting the underlying bit pattern"));
		
		FunctionCompletion fcp = new FunctionCompletion(aCp, "GEN", "void");
		fcp.setDefinedIn("TCSI");
		fcp.setShortDescription("X_VERIFY(gen(new event) != 0)");
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter(null, "event"));
		fcp.setParams(params);
		
		aCp.addCompletion(fcp);
	

	}
	

	@SuppressWarnings("unchecked")
	private void getNameSpaces(DefaultCompletionProvider provider, IRPModelElement selected) 
	{
		
		IRPPackage selectedPackage = RhapsodyOperation.getPackage(selected);
		if(selectedPackage==null)
		{
			return;
		}
		
		List<IRPDependency> dependencies = selectedPackage.getDependencies().toList();
		
		IRPPackage namespacePackage = RhapsodyOperation.getNamespacePackage(selectedPackage);
		
		
		if(namespacePackage!=null)
		{
			
			System.out.println("Namespace Package: "+namespacePackage.getName());
			if(namespacePackage!=selectedPackage)
			{
				dependencies.addAll(namespacePackage.getDependencies().toList());
			}
			
			RhapsodyNamespaceCompletion rnc = new RhapsodyNamespaceCompletion(provider, namespacePackage);
			provider.addCompletion(rnc);
			if(namespacePackage.equals(selectedPackage)==false)
			{
				dependencies.addAll(namespacePackage.getDependencies().toList());
			}
		}
		
		
		for(IRPDependency dependency: dependencies)
		{
			if(dependency.getDependsOn() instanceof IRPPackage)
			{
				IRPPackage p = RhapsodyOperation.getNamespacePackage(dependency.getDependsOn());
				if(p!=null)
				{
					RhapsodyNamespaceCompletion rnc = new RhapsodyNamespaceCompletion(provider, p); 
					provider.addCompletion(rnc);
					//load provider into cache....
					NamespaceCompletionProvider ncp = NamespaceCompletionProvider.GetNameSpaceProvider(rnc);
					
					if(ncp.hasNoNamespace())
					{
						ncp.addToOtherProvider(provider);
					}
					
					
					
				}
			}
				
		}
		
		IRPPackage packageStd = (IRPPackage)mySelectedOperation.getProject().findNestedElementRecursive("std", "Package");
		if(packageStd!=null)
		{
			RhapsodyNamespaceCompletion rnc = new RhapsodyNamespaceCompletion(provider,packageStd);
			provider.addCompletion(rnc);
			NamespaceCompletionProvider ncp = NamespaceCompletionProvider.GetNameSpaceProvider(rnc);		
		}
		
		IRPPackage packageOssi = (IRPPackage)mySelectedOperation.getProject().findNestedElementRecursive("OSSI", "Package");
		if(packageOssi!=null)
		{
			RhapsodyNamespaceCompletion rnc = new RhapsodyNamespaceCompletion(provider,packageStd);
			provider.addCompletion(rnc);
			NamespaceCompletionProvider ncp = NamespaceCompletionProvider.GetNameSpaceProvider(rnc);		
		}
		
		
		
	}

	
}

