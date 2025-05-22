package de.schlaich.gunnar.aiTools;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.Provider.Service;
import java.security.KeyManagementException;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCodeGenerator;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPOperation;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyHelper;
import de.schlaich.gunnar.rhapsody.utilities.RhapsodyOperation;

public class GeminiAPIClient
{

	private static final String API_KEY = System.getenv("GEMINI_API_KEY"); // API-Schlüssel aus Umgebungsvariable
	private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

	private Consumer<String> myTraceAction = null;

	private IRPApplication myApplication = null;

	public GeminiAPIClient(Consumer<String> aTraceAction, IRPApplication aApplication)
	{
		myApplication = aApplication;

		if (myApplication == null)
		{
			trace("No Rhapsody Application found.");

		}

		myTraceAction = aTraceAction;

		System.setProperty("https.protocols", "TLSv1.2");
		System.setProperty("com.ibm.jsse2.overrideDefaultTLS", "true");
		System.setProperty("com.ibm.jsse2.supportTLSv12", "true");

	}

	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "Gemini: " + aMessage;

		myTraceAction.accept(aMessage);
	}

	public String generateContent(String prompt) throws IOException
	{

		/*
		 * test with curl... curl -v -X POST -H "Content-Type: application/json" -d
		 * '{"contents":[{"parts":[{"text":"Erzähl mir einen Witz über Programmierer."}]
		 * }]}'
		 * "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key="
		 * 
		 */

		// check if API_KEY is set
		if (API_KEY == null || API_KEY.isEmpty())
		{
			trace("API key is not set. Please set the GEMINI_API_KEY environment variable.");
			throw new IOException("API key is not set.");
		}

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		ArrayNode contents = root.putArray("contents");

		ObjectNode part = mapper.createObjectNode();
		part.put("text", prompt);

		ObjectNode item = mapper.createObjectNode();
		item.set("parts", mapper.createArrayNode().add(part));
		contents.add(item);

		String jsonPayload = mapper.writeValueAsString(root);
		StringEntity entity = new StringEntity(jsonPayload);
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(API_ENDPOINT + "?key=" + API_KEY);

		httpPost.setHeader("Content-Type", "application/json");

		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity responseEntity = response.getEntity();

		ObjectMapper rmapper = new ObjectMapper();
		GeminiResponse rresponse = rmapper.readValue(EntityUtils.toString(responseEntity), GeminiResponse.class);

		String output = rresponse.candidates.get(0).content.parts.get(0).text;

		return output;

	}

	public void testGeminiAPI()
	{

		try
		{
			String prompt = "Erzähl mir einen Witz über Programmierer.";

			String json = generateContent(prompt);

			ObjectMapper mapper = new ObjectMapper();
			GeminiResponse response = mapper.readValue(json, GeminiResponse.class);

			String output = response.candidates.get(0).content.parts.get(0).text;
			trace("Antwort von Gemini:\n" + output);

		}
		catch (IOException e)
		{
			trace("Error on API-Request: " + e.getMessage());
		}

	}

	public void generateDescription(IRPOperation aOperation)
	{
		try
		{

			if (aOperation.getDescription().isEmpty() == false)
			{
				trace("Operation already has a description.");
				return;

			}

			String signature = RhapsodyOperation.getOperation_(aOperation);// .getOperation(aOperation);

			trace("Signature: " + signature);

			String body = aOperation.getBody();

			String operation = "\"" + signature + "\n{ " + body + "\n}" + "\"";

			String prompt = "what is the purpose of the operation following C++ operation?:\n\n```cpp\n" + operation
					+ "\n```  \n only summery, no rtf, no comments, use newline for line breaks, describe also the arguments and return values, use the same order as in the signature";

			String output = generateContent(prompt);
			output = output + "\n[gemini generated]\n";
			trace("Description from Gemini:\n" + output);

			if (aOperation.isReadOnly() == 0)
			{
				aOperation.setDescription(output);

			}
			else
			{
				trace("Operation is read only, no description set.");
			}

		}
		catch (IOException e)
		{
			trace("Error on API-request: " + e.getMessage());
		}

	}

	public void generateDescription(IRPClass aClass)
	{

		try
		{

			if (myApplication == null)
			{
				trace("No Rhapsody Application found.");
				return;
			}

			
			

			aClass.locateInBrowser();
			IRPCollection generateCollection = myApplication.getListOfSelectedElements();
			generateCollection.addItem(aClass);

			myApplication.generateElements(generateCollection);

			IRPCodeGenerator codeGenerator = myApplication.getTheCodeGeneratorInterface();

			IRPCollection col = myApplication.createNewCollection();
			col.addItem(aClass);

			myApplication.generateElements(col);

			

			String path = ASTHelper.getSourcePath(aClass, myApplication);

			String hFileContent = "";
			String cppFileContent = "";

			File hFile = new File(path + ".h");
			File cppFile = null;
			if (hFile.exists() == false)
			{
				trace("File does not exist: " + hFile.getAbsolutePath());
				return;
			}

			if (aClass.isATemplate() == 0)
			{
				cppFile = new File(path + ".cpp");

				if (cppFile.exists() == false)
				{
					trace("File does not exist: " + cppFile.getAbsolutePath());
					return;
				}

				// read cppFile into string
				StringBuilder cppFileContentBuilder = new StringBuilder();
				try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(cppFile)))
				{
					String line;
					while ((line = br.readLine()) != null)
					{
						cppFileContentBuilder.append(line).append("\n");
					}
				}
				catch (IOException e)
				{
					trace("Error reading file: " + e.getMessage());
				}
				cppFileContent = cppFileContentBuilder.toString();

			}

			// read hFile into string
			StringBuilder hFileContentBuilder = new StringBuilder();
			try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(hFile)))
			{
				String line;
				while ((line = br.readLine()) != null)
				{
					hFileContentBuilder.append(line).append("\n");
				}
			}
			catch (IOException e)
			{
				trace("Error reading file: " + e.getMessage());
			}

			hFileContent = hFileContentBuilder.toString();

			String prompt = "what is the purpose of the following C++ class?:\n\n```header File: \n" + hFileContent
					+ "\n ```  \n```cpp File: \n" + cppFileContent
					+ "\n```  \n only summery, no rtf, no comments, use newline for line breaks";

			String output = generateContent(prompt);
			output = output + "\n[gemini generated]\n";
			trace("Description from Gemini:\n" + output);
			
			if (aClass.getDescription().isEmpty() == true)
			{
				if (aClass.isReadOnly() == 0)
				{
					aClass.setDescription(output);

				}
				else
				{
					trace("Operation is read only, no description set.");
				}

			}
			
			//check for operations
			List<IRPOperation> operations = aClass.getOperations().toList();
			for (IRPOperation operation : operations)
			{
				
				generateDescription(operation);
				
			}
			


			
		}
		catch (IOException e)
		{
			trace("Error on API-request: " + e.getMessage());
		}

	}

}

class GeminiResponse
{
	public List<Candidate> candidates;
	public UsageMetadata usageMetadata;
	public String modelVersion;

	public static class Candidate
	{
		public Content content;
		public String finishReason;
		public double avgLogprobs;
	}

	public static class Content
	{
		public List<Part> parts;
		public String role;
	}

	public static class Part
	{
		public String text;
	}

	public static class UsageMetadata
	{
		public int promptTokenCount;
		public int candidatesTokenCount;
		public int totalTokenCount;
		public List<TokenDetail> promptTokensDetails;
		public List<TokenDetail> candidatesTokensDetails;
	}

	public static class TokenDetail
	{
		public String modality;
		public int tokenCount;
	}
}
