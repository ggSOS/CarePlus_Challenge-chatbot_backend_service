package br.com.chatbot.exception.type.swagger;

public class READMEInvalidException extends RuntimeException {
    public READMEInvalidException(String message) {
        super(message);
    }

    public READMEInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
