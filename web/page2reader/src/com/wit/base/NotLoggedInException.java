package com.wit.base;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NotLoggedInException extends Exception implements Serializable {

	public NotLoggedInException(){
	    super("NotLoggedInException");
	}
	
	public NotLoggedInException(String message){
        super(message);
    }
	
	public NotLoggedInException(String message, Throwable cause){
        super(message, cause);
    }
	
	public NotLoggedInException(Throwable cause){
        super(cause);
    }
	
}
