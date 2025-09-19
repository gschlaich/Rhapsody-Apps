package de.schlaich.gunnar.aiTools.mcp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Json
{
	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping() // wichtig, damit z.B. '<' nicht zu \\u003c
																				// wird
			.create();

	private Json()
	{
	} // verhindert Instanziierung

	public static Gson gson()
	{
		return GSON;
	}
}