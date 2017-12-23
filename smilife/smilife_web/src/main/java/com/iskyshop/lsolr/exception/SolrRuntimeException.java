package com.iskyshop.lsolr.exception;

public class SolrRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public SolrRuntimeException(Throwable cause){
		super(cause);
	}
	
	public SolrRuntimeException(String msg,Throwable cause){
		super(msg, cause);
	}
	
}
