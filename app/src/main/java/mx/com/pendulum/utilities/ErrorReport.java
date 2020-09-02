package mx.com.pendulum.utilities;

import android.os.Bundle;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorReport {
    public static final String newLine = System.getProperty("line.separator");

    public static final String KEY_TYPE = "mx.com.pendulum.utilities.errors.ErrorReport.KEY_TYPE";
    public static final String KEY_MESSAGE = "mx.com.pendulum.utilities.errors.ErrorReport.KEY_MESSAGE";
    public static final String KEY_STACKTRACE = "mx.com.pendulum.utilities.errors.ErrorReport.KEY_STACKTRACE";

    private String type;
    private String message;
    private String stackTrace;
    private Exception exception;
    private boolean warning;


    public ErrorReport(String type, String message, String stackTrace, Exception exception) {
        this.type = type;
        this.message = message;
        this.stackTrace = stackTrace;
        this.exception = exception;

        setWarning(false);

    }

    public ErrorReport(Exception exception) {
        this("", "", "", exception);

        if (exception != null) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            setType(exception.getClass().getSimpleName());
            setMessage(exception.getMessage());
            setStackTrace(sw.toString());
        }
    }


    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        bundle.putString(KEY_MESSAGE, message);
        bundle.putString(KEY_STACKTRACE, stackTrace);
        return bundle;
    }

    public String toString() {
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder
                .append("Type").append(newLine).append(getType()).append(newLine)
                .append("Message: ").append(newLine).append(getMessage()).append(newLine).append(newLine)
                .append("Stack Trace: ").append(newLine).append(getStackTrace());

        return errorBuilder.toString();
    }
}
