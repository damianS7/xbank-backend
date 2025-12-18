package com.damian.xBank.shared.exception;

public class ApplicationException extends RuntimeException {

    private final Object resourceId;
    private final String errorCode;
    private final Object[] args;

    public ApplicationException(
            String errorCode,
            Object resourceId,
            Object[] args
    ) {
        super(errorCode);
        this.resourceId = resourceId;
        this.errorCode = errorCode;
        this.args = args;
    }

    // TODO for removal
    public ApplicationException(
            String errorCode
    ) {
        this(errorCode, null, null);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getResourceId() {
        return resourceId;
    }

    public Object[] getArgs() {
        return args;
    }
}
