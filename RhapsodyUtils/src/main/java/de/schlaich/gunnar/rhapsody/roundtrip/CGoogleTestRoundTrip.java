package de.schlaich.gunnar.rhapsody.roundtrip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComponent;
import com.telelogic.rhapsody.core.IRPFile;
import com.telelogic.rhapsody.core.IRPFileFragment;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;

public class CGoogleTestRoundTrip
{

	private class TestSpec {
		
		public String myTestType = null;
		public String myMacro = null;
		public String mySuite = null;
		public String myName = null;
		
		public String myBody = null;
		
		public TestSpec( String macro, String suite, String name, String body)
		{
			
			myMacro = macro;
			mySuite = suite;
			myName = name;
			myBody = body;
		}
		
	
		
		public String name() {
			 return myName;
		}
		
		public String suite() {
			return mySuite;
		}
		
		public String macro() {
			return myMacro;
		}
		
		
		public String body() {
			return myBody;
		}
		
		public String test() {
			StringBuilder sb = new StringBuilder();
			
			sb.append(myMacro);
			sb.append("(");
			sb.append(mySuite);
			sb.append(", ");
			sb.append(myName);
			sb.append(") {\n");
			sb.append("\t");
			sb.append(myBody);
			sb.append("\n}\n");
			return sb.toString();
		}
		
		
	}
	
	private IRPApplication myApplication = null;
	
	private static final Pattern RE_TEST_F = Pattern.compile(
			"TEST_F\\s*\\(\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*,\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*\\)\\s*\\{?",
			Pattern.MULTILINE);

	private static final Pattern RE_TEST = Pattern.compile(
			"TEST\\s*\\(\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*,\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*\\)\\s*\\{?",
			Pattern.MULTILINE);

	private static final Pattern RE_TEST_P = Pattern.compile(
			"TEST_P\\s*\\(\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*,\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*\\)\\s*\\{?",
			Pattern.MULTILINE);
	
	 private static final Pattern RE_TEST_HEADER = Pattern.compile(
		        "(TEST_F|TEST_P|TEST)\\s*\\(\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*,\\s*"
		      + "([A-Za-z_][A-Za-z0-9_]*)\\s*\\)\\s*\\{",
		        Pattern.MULTILINE);

	

	public CGoogleTestRoundTrip(IRPApplication aApplication)
    {
        myApplication = aApplication;
    }	
	
	
	
	public static  CGoogleTestRoundTrip getInstance(IRPApplication aApplication)
	{
		return new CGoogleTestRoundTrip(aApplication);
	}
	
	public boolean startRoundTrip(IRPFile aFile)
    {
		if (aFile == null)
		{
			return false;
		}

		
		//System.out.println(" Dir: " + ASTHelper.getSourcePath(aType, myApplication).getAbsolutePath());
		
		File sourceFile = ASTHelper.getSourcePath(aFile, myApplication,".cpp");
		
		
		
		if (sourceFile.exists() == false)
		{
			System.out.println("Header File: " + sourceFile.getAbsolutePath() + " does not exist!");
			return false;
		}
		
		String source = "";//Files.readString(headerFile.getAbsolutePath());

		//read the file
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
			StringBuilder content = new StringBuilder();
			String line = null;
			
			
			
			while ((line = reader.readLine()) != null)
			{
				
				line.trim();
				content.append(line).append("\n");
			}
			
			source = content.toString();
				
			
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(source==null)
		{
			return false;
		}
		
		
		Map<String, TestSpec> mapF = new LinkedHashMap<>();
	    Map<String, TestSpec> map  = new LinkedHashMap<>();
	    Map<String, TestSpec> mapP = new LinkedHashMap<>();
		List<TestSpec> tests = new ArrayList<TestSpec>();
		
		
		

	     // ─── 4. Suchen und Einsortieren ─────────────────────────────────────────
	     //scan(source, RE_TEST_F, "TEST_F", mapF);
	     //scan(source, RE_TEST,   "TEST",   map);
	     //scan(source, RE_TEST_P, "TEST_P", mapP);
	     
	     
	     Matcher m = RE_TEST_HEADER.matcher(source);
	        while (m.find()) {
	            String macro  = m.group(1); // TEST / TEST_F / TEST_P
	            String suite  = m.group(2);
	            String name   = m.group(3);

	            int bodyStart = m.end() - 1;           // Position des ‘{’
	            int bodyEnd   = matchBrace(source, bodyStart);
	            if (bodyEnd == -1) {
	                System.err.println("Kein passendes } für " + name);
	                continue;
	            }
	            String body = source.substring(bodyStart + 1, bodyEnd);

	            TestSpec spec = new TestSpec(macro, suite, name, body);
	            
	            tests.add(spec);
	            
	            switch (macro) {
	                case "TEST_F": 
	                	mapF.put(name, spec);
	                	break;
	                case "TEST_P": 
	                	mapP.put(name, spec);
	                	break;
	                case "TEST":
	                	map.put(name,  spec);
	                	break;
	            }
	        }
	     
	     List<IRPFileFragment> fileFragments = aFile.getFileFragments().toList();
	     
	    
	    
	     
	     
	     for(IRPFileFragment fileFragment:fileFragments)
	     {

	    	 if(fileFragment.getName().equals("Tests"))
	    	 {
	    		 StringBuilder sb = new StringBuilder();
	    		 
	    		 for(TestSpec spec:tests)
	    		 {
	    			 sb.append("//");	    			 
	    			 sb.append(spec.name());
	    			 sb.append("\n");
	    			 sb.append(spec.test());
	    			 sb.append("\n");
	    		 }
	    		 
	    		 fileFragment.setFragmentText(sb.toString());
 
	    	 }

	     }
	     
	     
	     
	     
	    
	     /*   
	     
	     String testName = aType.getName();
	     
	     TestSpec testSpec =   mapF.get(testName);
	     
	     if(testSpec == null)
	     {
	    	 testSpec = map.get(testName);
	     }
	     
	     if(testSpec == null)
	     {
	    	 testSpec = mapP.get(testName);
	     }
	     
	     if(testSpec==null)
	     {
	    	return false; 
	     }
	     
	     
	     aType.setDeclaration( testSpec.test());
	      */
		
		return true;
        
    }
	
	
	private void scan(String src, Pattern p, String macro, Map<String, TestSpec> target)
	{
		Matcher m = p.matcher(src);
		while (m.find())
		{
			String suite = m.group(1);
			String name = m.group(2);
			String full = m.group(0);
			target.put(name, new TestSpec(macro, suite, name, full));
		}
	}
	
	private int matchBrace(String text, int bodyStart) {
        int level = 0;
        for (int i = bodyStart; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') level++;
            else if (c == '}') {
                level--;
                if (level == 0)
                    return i;      // passender Abschluss gefunden
            }
        }
        return -1;                 // unbalanciert
    }

	private void print(String title, Map<String, TestSpec> map)
	{
		System.out.println("\n" + title);
		map.values().forEach(ts -> System.out.printf("  %-20s  (Suite=%s,   Zeile='%s')%n", ts.name(), ts.suite(),
				ts.body().trim()));
	}
	
	

}
