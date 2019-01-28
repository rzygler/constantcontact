package com.boringguys.constantcontact.v2.exceptions;

public class TooManyContactsException extends Exception
{
    public TooManyContactsException(String errorMessage)
    {
        super(errorMessage);
    }
}
