package com.project.youtlix.common.domain;

/** Base unchecked exception for invalid domain operations. */
public class DomainException extends RuntimeException {
    public DomainException(String message) { super(message); }
}
