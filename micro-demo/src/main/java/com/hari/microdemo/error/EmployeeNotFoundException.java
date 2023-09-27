package com.hari.microdemo.error;

public class EmployeeNotFoundException extends RuntimeException{
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
