package de.schlaich.gunnar.aiTools;

import java.io.IOException;
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


public class GeminiAPIClient
{

	private static final String API_KEY = System.getenv("GEMINI_API_KEY"); // API-Schlüssel aus Umgebungsvariable
    private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    
    private Consumer<String> myTraceAction = null;
	
	public GeminiAPIClient(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
		
		System.setProperty("https.protocols", "TLSv1.2");
		System.setProperty("com.ibm.jsse2.overrideDefaultTLS", "true")
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
        
		
		HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(API_ENDPOINT + "?key=" + API_KEY);

        httpPost.setHeader("Content-Type", "application/json");

        String jsonPayload = String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", prompt);
        StringEntity entity = new StringEntity(jsonPayload);
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {
            return EntityUtils.toString(responseEntity);
        } else {
            return null;
        }
    }
	
	public void testGeminiAPI()
	{
	
		// Test the Gemini API
		
		try 
		{
            String prompt = "Erzähl mir einen Witz über Programmierer.";
            String response = generateContent(prompt);
            trace(response);
            
            // Parse the response (if needed)
          	/*
			 * 
			 * exampe response: { Gemini: { "candidates": [ { "content": { "parts": [ {
			 * "text":
			 * "Warum verwechseln Programmierer Halloween und Weihnachten?\n\nWeil Okt 31 == Dez 25 ist!\n"
			 * } ], "role": "model" }, "finishReason": "STOP", "avgLogprobs":
			 * -0.25974987030029295 } ], "usageMetadata": { "promptTokenCount": 13,
			 * "candidatesTokenCount": 25, "totalTokenCount": 38, "promptTokensDetails": [ {
			 * "modality": "TEXT", "tokenCount": 13 } ], "candidatesTokensDetails": [ {
			 * "modality": "TEXT", "tokenCount": 25 } ] }, "modelVersion":
			 * "gemini-2.0-flash" }
			 * 
			 * 
			 * 
			 * 
			 */
            

            
            
        } 
		catch (IOException e) {
            System.err.println("Fehler bei der API-Anfrage: " + e.getMessage());
        }
		
	
	}
	

	
}
