package br.com.chatbot.exception.type.swagger;

public class READMENotFoundException extends RuntimeException {
    public READMENotFoundException(String message) {
        super(message);
    }

    public READMENotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
