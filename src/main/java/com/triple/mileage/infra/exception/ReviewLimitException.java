package com.triple.mileage.infra.exception;

public class ReviewLimitException extends RuntimeException{

    public ReviewLimitException() {
        super();
    }

    public ReviewLimitException(String message) {
        super(message);
    }

    public ReviewLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReviewLimitException(Throwable cause) {
        super(cause);
    }
}
