package me.igromov.bank.exception;

public class InvalidAccountParametersException extends ApiRuntimeException {
    public InvalidAccountParametersException(String message) {
        super(message);
    }
}
