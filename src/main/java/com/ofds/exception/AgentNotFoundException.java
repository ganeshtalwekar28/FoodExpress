package com.ofds.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AgentNotFoundException extends Exception {

    private static final long serialVersionUID = -4903452964892993724L;

    public AgentNotFoundException(String message) {
        super(message);
    }
}