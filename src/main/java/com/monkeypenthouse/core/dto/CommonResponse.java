package com.monkeypenthouse.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.monkeypenthouse.core.constant.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    private int statusCode;
    private String responseMessage;
    private T data;

    public CommonResponse() {
        setStatusCode(ResponseCode.SUCCESS.getHttpStatus().value());
        setResponseMessage(ResponseCode.SUCCESS.getMessage());
    }

    public CommonResponse(ResponseCode responseCode) {
        setStatusCode(responseCode.getHttpStatus().value());
        setResponseMessage(responseCode.getMessage());
    }

    public CommonResponse(ResponseCode responseCode, String message) {
        setStatusCode(responseCode.getHttpStatus().value());
        setResponseMessage(responseCode.getMessage() + ": " + message);
    }
}
