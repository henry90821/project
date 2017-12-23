package com.smi.mc.exception;


public class AtomicServiceException extends Exception
{

    public AtomicServiceException(String message)
    {
        super(message);
    }

    public AtomicServiceException(Throwable throwable)
    {
        super(throwable);
    }

    private static final long serialVersionUID = 2323789732704293824L;
}
