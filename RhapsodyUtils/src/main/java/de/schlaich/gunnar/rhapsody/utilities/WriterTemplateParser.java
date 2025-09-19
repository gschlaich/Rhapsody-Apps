package de.schlaich.gunnar.rhapsody.utilities;



import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPModelElement;

public class WriterTemplateParser
{
	
	private Consumer<String> myTraceAction = null;
	
	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "WriterTempplateParser: " + aMessage;

		myTraceAction.accept(aMessage);
	}

	
	public WriterTemplateParser(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
	}
	
	public String parse(IRPModelElement aModelElement, boolean aImplementation)
	{
		if (aModelElement instanceof IRPAttribute)
		{
			
			String template = null;
			if(aImplementation)
			{
				template = aModelElement.getPropertyValue("CPP_CG.WriterTemplates.AttributeImp");
				if (template == null || template.isEmpty())
				{
					trace("parse: no implementation template defined for " + aModelElement.getName());
					return "";
				}
			}
			else
			{
				template = aModelElement.getPropertyValue("CPP_CG.WriterTemplates.AttributeSpec");
				if (template == null || template.isEmpty())
				{
					trace("parse: no specification template defined for " + aModelElement.getName());
					return "";
				}
			}
			
			return parseTemplate((IRPAttribute) aModelElement, template);
		}

		trace("parse: unsupported model element type: " + aModelElement.getClass().getName());
		return "";
	}
	
	
	//example attribute specification: 
	// [[$description
	// ]]$prolog[[$friend ]][[template <$parameters> ]][[$extern ]][[$static ]][[$const ]][[$predecl_kw ]]$type[[ $indecl_kw]][[ $name]][[ : $bitfield]][[ $postdecl_kw]]$init;$epilog[[		$annotation]]
	
	public String parseTemplate(IRPAttribute aAttribute, String template)
	{
		
		List<TemplateElement> elements = splitIntoElements(template);
		
		
		for (TemplateElement e : elements)
		{
			if (e.key == null)
			{
				continue;
			}
			
			switch (e.key) 
            {
			
			case "$description":
				e.setText(aAttribute.getDescription());
				break;
				
			case "$prolog":
				e.setText(aAttribute.getPropertyValue("CPP_CG.Attribute.SpecificationProlog"));
				break;
				
			case "$extern":
				if (aAttribute.getIsStatic() == 1)
                {
                    e.setText("static");
                }
				break;
				
			case "$const":

				if (aAttribute.getIsConstant() == 1)
                {
                    e.setText("const");
                }
				break;
			
			case "$predecl_kw":
			
				// dont know what this is supposed to be
				break;
			
			case "$type":
			
				e.setText(aAttribute.getType().getName());
				break;
			
			case "$indecl_kw":
			
				// dont know what this is supposed to be
				break;
			
			case "$name":
			
				e.setText(aAttribute.getName());
				break;
			
			case "$bitfield":
				
				String bitField = aAttribute.getPropertyValue("CPP_CG.Attribute.BitField");
				
				if (bitField != null && !bitField.isEmpty())
				{
				
					e.setText(aAttribute.getPropertyValue("CPP_CG.Attribute.BitField"));
				}
				
				break;
			
			case "$postdecl_kw":
			
				// dont know what this is supposed to be
				break;
			case "$init":
				
				String defaultValue = aAttribute.getDefaultValue();
				
				if (defaultValue != null && !defaultValue.isEmpty())
				{

					String initStyle = aAttribute.getPropertyValue("CPP_CG.Attribute.InitializationStyle");
					
					if(initStyle =="ByAssignment")
					{
						e.setText(" = " + defaultValue);
					}
					else
					{
						e.setText("(" + defaultValue + ")");
					}
				}
			
				// dont know what this is supposed to be
				break;
			
			case "$epilog":
            
                e.setText(aAttribute.getPropertyValue("CPP_CG.Attribute.SpecificationEpilog"));
                break;
                
			case "$annotation":
            
               //dont know what this is supposed to be
				break;
            
            default:
            
                // do nothing, this is not a field we know
            	break;
            }
            
		}
		
		String result = "";
		
		for (TemplateElement e : elements)
		{
			if (e.found)
			{
				result += e.inner;
			}
			else if(e.key == null)
			{
				result += e.raw; // normaler Text, ohne $-Feld
			}		
		}

		return result;

	}
	
	
	
	
	// Repräsentiert ein Segment: entweder ein [[...]]-Feld oder normaler Text
    class TemplateElement 
    {
        public final boolean bracketed; // true = stammt aus [[...]]
        public boolean found;    // normaler Text, ohne [[...]]-Felder
        public final String raw;        // exakt wie im Input (inkl. [[ ]] bei Feldern)
        public String inner;      // nur der Inhalt zwischen [[ and ]], sonst == raw
        public String key;        // erstes $-Wort (z.B. "$type"), oder null

        public TemplateElement(boolean bracketed, String raw)
        {
            this.bracketed = bracketed;
            this.raw = raw;
			if (bracketed)
			{
				this.inner = raw.substring(2, raw.length() - 2); // ohne [[ ]]
			}
			else
			{
				this.inner = raw;
			}
			   
            this.key = extractKey(this.inner);
        }
        
		public void setText(String replacement)
		{
			inner = inner.replace(this.key, replacement);
			found = true;
		}

        /** Optionaler Helfer: key ohne führendes '$' (oder null). */
        public String keyName() 
        {
            if (key == null) return null;
            return key.startsWith("$") ? key.substring(1) : key;
        }

        @Override public String toString() 
        {
            return String.format("%s raw=\"%s\" key=%s",
                    bracketed ? "FIELD" : "TEXT ",
                    raw.replace("\n", "\\n").replace("\t", "\\t"),
                    key);
        }
    }
    

    // $-Identifier: beginnt mit $, dann [A-Za-z_], dann [A-Za-z0-9_]*
    private static final Pattern KEY_PATTERN = Pattern.compile("\\$[A-Za-z_][A-Za-z0-9_]*");

    private String extractKey(String text) {
        Matcher m = KEY_PATTERN.matcher(text);
        return m.find() ? m.group() : null;
    }
    
    
    List<TemplateElement> splitIntoElements(String s) {
        List<TemplateElement> out = new ArrayList<>();
        int i = 0, n = s.length();

        while (i < n) {
            // 1) [[...]]
            if (i + 1 < n && s.charAt(i) == '[' && s.charAt(i + 1) == '[') {
                int end = s.indexOf("]]", i + 2);
                if (end != -1) {
                    out.add(new TemplateElement(true, s.substring(i, end + 2)));
                    i = end + 2;
                    continue;
                }
                // Kein schließendes "]]": dann das aktuelle Zeichen als CHAR ausgeben
                out.add(new TemplateElement(false, String.valueOf(s.charAt(i))));
                i++;
                continue;
            }

            // 2) $Key mit nur Buchstaben
            if (s.charAt(i) == '$' && i + 1 < n && Character.isLetter(s.charAt(i + 1))) {
                int j = i + 2;
                while (j < n && Character.isLetter(s.charAt(j))) j++;
                out.add(new TemplateElement(false, s.substring(i, j)));
                i = j;
                continue;
           }

            // 3) Sonstiges Zeichen -> einzelnes CHAR-Element
            out.add(new TemplateElement(false, String.valueOf(s.charAt(i))));
            i++;
        }
        
        for (TemplateElement e : out)
		{
			trace("splitIntoElements: " + e.toString());
		}

        return out;
    }

	




}
