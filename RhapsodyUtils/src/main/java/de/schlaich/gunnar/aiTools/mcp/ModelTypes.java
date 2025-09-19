package de.schlaich.gunnar.aiTools.mcp;

public final class ModelTypes
{
	public static boolean isLogicalClassifier(String kind)
	{
		if ("Class".equals(kind)) return true;
		if ("Actor".equals(kind)) return true;
		if ("Interface".equals(kind)) return true;
		if ("Block".equals(kind)) return true;
		return false;
	}
}