package de.schlaich.gunnar.aiTools.copilot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CopilotRestClient
{

	private static final String BASE_URL = "http://127.0.0.1:8766";

	private final ObjectMapper mapper = new ObjectMapper();

	// -------------------------------------------------
	// Register generated C++ class context
	// -------------------------------------------------

	public String registerContext(String className, String header, String source) throws IOException
	{

		Map<String, Object> request = new HashMap<>();

		request.put("className", className);
		request.put("language", "cpp");
		request.put("header", header);
		request.put("source", source);

		Map<String, Object> response = post("/ai/context", request);

		return (String) response.get("contextId");
	}

	// -------------------------------------------------
	// Request AI code completion
	// -------------------------------------------------

	public String complete(String contextId, String methodName, String methodSignature, String prefix, String suffix)
			throws IOException
	{

		Map<String, Object> request = new HashMap<>();

		request.put("contextId", contextId);
		request.put("methodName", methodName);
		request.put("methodSignature", methodSignature);
		request.put("prefix", prefix);
		request.put("suffix", suffix);

		Map<String, Object> response = post("/ai/complete", request);

		return (String) response.get("completion");
	}

	// -------------------------------------------------
	// Remove cached context
	// -------------------------------------------------

	public void removeContext(String contextId) throws IOException
	{

		if (contextId == null)
		{
			return;
		}

		request("DELETE", "/ai/context/" + contextId, null);
	}

	// -------------------------------------------------
	// Health check
	// -------------------------------------------------

	public boolean isAvailable()
	{

		try
		{
			Map<String, Object> result = request("GET", "/health", null);

			return "ok".equals(result.get("status"));

		}
		catch(IOException e)
		{
			return false;
		}
	}

	// -------------------------------------------------
	// HTTP helpers
	// -------------------------------------------------

	private Map<String, Object> post(String endpoint, Object body) throws IOException
	{

		return request("POST", endpoint, body);
	}

	private Map<String, Object> request(String method, String endpoint, Object body) throws IOException
	{

		HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + endpoint).openConnection();

		connection.setRequestMethod(method);
		connection.setConnectTimeout(2000);
		connection.setReadTimeout(130000);
		connection.setRequestProperty("Accept", "application/json");

		try
		{
			if (body != null)
			{
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

				byte[] data = mapper.writeValueAsBytes(body);

				try (OutputStream out = connection.getOutputStream())
				{
					out.write(data);
				}
			}

			int status = connection.getResponseCode();

			InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();

			if (stream == null)
			{
				throw new IOException("HTTP " + status + ": no response body");
			}

			try (InputStream in = stream)
			{
				Map<String, Object> response = mapper.readValue(in, new TypeReference<Map<String, Object>>()
				{
				});

				if (status < 200 || status >= 300)
				{
					throw new IOException("HTTP " + status + ": " + response.get("error"));
				}

				return response;
			}

		}
		finally
		{
			connection.disconnect();
		}
	}
}
