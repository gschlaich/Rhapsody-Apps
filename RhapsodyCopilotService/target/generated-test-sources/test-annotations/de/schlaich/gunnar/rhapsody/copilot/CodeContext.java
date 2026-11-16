package de.schlaich.gunnar.rhapsody.copilot;

public class CodeContext {

    public String className;
    public String language = "cpp";
    public String header;
    public String source;

    public CodeContext() {
    }

    public static class CompletionRequest {

        public String contextId;
        public String methodName;
        public String methodSignature;
        public String prefix;
        public String suffix;

        public CompletionRequest() {
        }
    }
}
