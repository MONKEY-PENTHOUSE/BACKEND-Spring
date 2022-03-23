package com.monkeypenthouse.core.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.monkeypenthouse.core.constant.ResponseCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class CommonResponseMaker {
    public <T> CommonResponse<T> makeSucceedCommonResponse(
            T response
    ) {
        final CommonResponse<T> wrappedResponse = new CommonResponse<>();
        wrappedResponse.data = response;
        return wrappedResponse;
    }

    public CommonResponse<Void> makeEmptyInfoCommonResponse(
            ResponseCode responseCode
    ) {
        final CommonResponse<Void> commonResponse = new CommonResponse<>();
        commonResponse.responseMessage = responseCode.getMessage();
        return commonResponse;
    }

    public CommonResponse<Void> makeFailedCommonResponse(
            ResponseCode responseCode
    ) {
        final CommonResponse<Void> commonResponse = new CommonResponse<>(responseCode);
        return commonResponse;
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class CommonResponse<T> {

        private int detailStatus;
        private String responseMessage;
        private T data;

        private CommonResponse() {
            detailStatus = ResponseCode.SUCCESS.getDetailStatus();
            responseMessage = ResponseCode.SUCCESS.getMessage();
        }

        private CommonResponse(ResponseCode responseCode) {
            detailStatus = responseCode.getDetailStatus();
            responseMessage = responseCode.getMessage();
        }

        private CommonResponse(ResponseCode responseCode, String message) {
            detailStatus = responseCode.getDetailStatus();
            responseMessage = responseCode.getMessage() + ": " + message;
        }
    }
}
