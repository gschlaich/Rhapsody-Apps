package de.schlaich.gunnar.chatGPT;
import com.lilittlecat.chatgpt.offical.ChatGPT;
import com.lilittlecat.chatgpt.offical.exception.BizException;

public class AskIssues {

	public static void main(String[] args) {

		AskIssues ai = new AskIssues("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");
		
		String errorCode = " X_TRACE_RELEASE(X_INFO) << \"credentials from \" << autoConnectSSID  \"known, do auto connect\";\r\n";
		
		System.out.println("Ask for wrong Syntax: " + errorCode + "...");
		
		String result = ai.syntaxError(errorCode);

		System.out.println(result); // will be "\n\nHello! How may I assist you today?"
	}
	
	private ChatGPT myChatGPT = null;
	
	private String myApiKey = null;
	
	//private String myApiKey = "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";
	
	public AskIssues(String aApiKey)
	{
		myApiKey = aApiKey;
		
		myChatGPT = new ChatGPT(myApiKey);
	}
	
	private String question(String aProlog, String aCodeSnippet)
	{
		String ret = "";
		if(myChatGPT==null)
		{
			return ret;
		}
		
		int i = 3;
		
		while(i!=0)
		{
		
			try
			{
				ret = myChatGPT.ask(aProlog + aCodeSnippet);
				break;
			}
			catch (BizException e) {
				ret = e.getMessage();
				i--;
			}
		}
			
		return ret;
	}
	
	public String syntaxError(String aCodeSnippet)
	{
		
		return question("C++ Error: ", aCodeSnippet);
		
	}
	
	public String optimize(String aCodeSnippet)
	{
		return question("Can you optimize following code: ",aCodeSnippet);
	}
	
	
	
}
