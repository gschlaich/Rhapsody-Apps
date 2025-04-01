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
    private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=" + API_KEY; // Überprüfe die aktuelle Gemini API-URL
    
    private Consumer<String> myTraceAction = null;
	
	public GeminiAPIClient(Consumer<String> aTraceAction)
	{
		myTraceAction = aTraceAction;
		
		
		
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
        
		/* test with curl...
		 curl -v -X POST  -H "Content-Type: application/json" -d '{"contents":[{"parts":[{"text":"Erzähl mir einen Witz über Programmierer."}]}]}' "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" 
		 
		 */
		
		
		
		if (Security.getProvider("BC") == null) 
		{
		    
			BouncyCastleProvider bcProvider = new BouncyCastleProvider();
			Set<Service>bcServices = bcProvider.getServices();
			
			
			for(Service s:bcServices)
			{
				trace("BouncyCastleProvider Service: " + s.toString());
			}
			
			
			Security.addProvider(new BouncyCastleProvider());
		    
		   
		}
		
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
		
		try {
		    SSLContext sslContext = SSLContext.getInstance("TLSv1.2"); // Oder "TLS"
		    sslContext.init(null, new TrustManager[]{new X509TrustManager() {
		        public X509Certificate[] getAcceptedIssuers() {
		            return null;
		        }
		        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
		        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
		    }}, null);

		    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		    HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
		    System.setProperty("https.cipherSuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"); // Beispiel!

		} catch (NoSuchAlgorithmException | KeyManagementException e) {
		    e.printStackTrace();
		}
	
		
		try 
		{
            String prompt = "Erzähl mir einen Witz über Programmierer.";
            String response = generateContent(prompt);
            trace(response);
        } 
		catch (IOException e) {
            System.err.println("Fehler bei der API-Anfrage: " + e.getMessage());
        }
		
	
	}
	
}
