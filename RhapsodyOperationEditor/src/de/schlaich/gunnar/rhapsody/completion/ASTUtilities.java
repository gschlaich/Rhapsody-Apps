package de.schlaich.gunnar.rhapsody.completion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.index.EmptyCIndex;
import org.eclipse.cdt.internal.core.parser.scanner.CharArray;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.core.runtime.CoreException;
import org.fife.ui.autocomplete.Completion;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPClassifier;

import de.schlaich.gunnar.rhapsody.completionprovider.ClassifierCompletionProvider;
import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;;


public class ASTUtilities {
	
	private static String DefaultFilename = "SourceCode.cpp";
	public final static String Prolog = "void checkOperation() { \n";
	private static String Epilog = "\n }";

	
		public static IASTTranslationUnit getTranslationUnit(String aOperationBody, ClassifierCompletionProvider aClassifierCompletionProvider)
		{
			StringBuilder code = new StringBuilder();
			if(aClassifierCompletionProvider!=null)
			{
				List<Completion> completions = aClassifierCompletionProvider.getCompletions();
				IRPClassifier cl = aClassifierCompletionProvider.getClassifier();
				String ownNameSpace = RhapsodyOperation.getNamespace(cl);
				for(Completion c : completions)
				{
					if(c instanceof RhapsodyClassifierCompletion)
					{
						RhapsodyClassifierCompletion rcc = (RhapsodyClassifierCompletion)c;
						IRPClassifier irpc = rcc.getIRPClassifier(false);
						if(irpc instanceof IRPClass)
						{
							String namespace = RhapsodyOperation.getNamespace(irpc);
							if((namespace!=null)&&(namespace.equals("")==false)&&(namespace.equals(ownNameSpace)==false))
							{
								code.append("\nnamespace ");
								code.append(namespace);
								code.append(" { \n");
								code.append("    class ");
								code.append(irpc.getName());
								code.append(";\n}\n");	
							}
							else
							{
								code.append("\nclass ");
								code.append(irpc.getName());
								code.append(";\n");
							}
						
						}
						
					}
				}
				
			}
			
			code.append(Prolog);
			code.append(aOperationBody);
			code.append(Epilog);
			String operation = code.toString();
			FileContent fileContent = new InternalFileContent(DefaultFilename, new CharArray(operation));
			
			return ASTHelper.getTranslationUnit(fileContent);
			
		}
		
	

}
