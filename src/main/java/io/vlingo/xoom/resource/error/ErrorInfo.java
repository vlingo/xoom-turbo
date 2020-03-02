package io.vlingo.xoom.resource.error;

public class ErrorInfo {

    private String message;
    private String exception;

    public ErrorInfo() {
    }

    public ErrorInfo(Throwable throwable) {
        this.message = throwable.getLocalizedMessage();
        this.exception = throwable.getClass().getName();
    }

    public ErrorInfo(String message, String exception) {
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "message='" + message + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}
