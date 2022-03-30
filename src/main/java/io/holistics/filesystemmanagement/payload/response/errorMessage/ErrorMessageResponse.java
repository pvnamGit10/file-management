package io.holistics.filesystemmanagement.payload.response.errorMessage;

import lombok.Data;

@Data
public class ErrorMessageResponse {
    private String message;

    public ErrorMessageResponse(String message) {
        this.message = message;
    }
}
