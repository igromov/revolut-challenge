package me.igromov.exchanger.exception;

public class InvalidAccountParametersException extends ApiRuntimeException {
    public InvalidAccountParametersException(String message) {
        super(message);
    }
}
