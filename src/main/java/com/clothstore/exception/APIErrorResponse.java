
package com.clothstore.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class APIErrorResponse {
    private int status;
    private String message;
    private Map<String, String> errors;
}
