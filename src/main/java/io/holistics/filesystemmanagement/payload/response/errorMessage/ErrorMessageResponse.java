package io.holistics.filesystemmanagement.payload.response.errorMessage;

import lombok.Data;

@Data
public class ErrorMessageResponse {
    private String message;
    private String status;

    public ErrorMessageResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
