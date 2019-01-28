package com.boringguys.constantcontact.v2.exceptions;

public class PayloadTooLargeException extends Exception
{
    public PayloadTooLargeException(String errorMessage)
    {
        super(errorMessage);
    }
}
