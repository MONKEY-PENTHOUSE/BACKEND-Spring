package com.monkeypenthouse.core.component;

import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.CommonResponse;
import org.springframework.stereotype.Component;

@Component
public class CommonResponseMaker {
    public <T> CommonResponse<T> makeSucceedCommonResponse(
            T response
    ) {
        final CommonResponse<T> wrappedResponse = new CommonResponse<>();
        wrappedResponse.setData(response);
        return wrappedResponse;
    }

    public CommonResponse<Void> makeEmptyInfoCommonResponse(
            ResponseCode responseCode
    ) {
        final CommonResponse<Void> commonResponse = new CommonResponse<>();
        commonResponse.setStatusCode(responseCode.getHttpStatus().value());
        commonResponse.setResponseMessage(responseCode.getMessage());
        return commonResponse;
    }
}
