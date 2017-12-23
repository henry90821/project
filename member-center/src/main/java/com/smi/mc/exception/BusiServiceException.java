/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.smi.mc.exception;


public class BusiServiceException extends Exception
{

    public BusiServiceException(String message)
    {
        super(message);
    }

    public BusiServiceException(Throwable throwable)
    {
        super(throwable);
    }

    public BusiServiceException(String Status, String ErrorCode, String ErrorMessage)
    {
        this.ErrorCode = ErrorCode;
        this.Status = Status;
        this.ErrorMessage = ErrorMessage;
    }

    public Throwable fillInStackTrace()
    {
        return this;
    }

    public String getStatus()
    {
        return Status;
    }

    public void setStatus(String status)
    {
        Status = status;
    }

    public String getErrorCode()
    {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode)
    {
        ErrorCode = errorCode;
    }

    public String getErrorMessage()
    {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        ErrorMessage = errorMessage;
    }

    private static final long serialVersionUID = 2323789732704293824L;
    private String Status;
    private String ErrorCode;
    private String ErrorMessage;
}
