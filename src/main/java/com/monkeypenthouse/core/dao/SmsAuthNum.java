package com.monkeypenthouse.core.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "sms-auth-num", timeToLive = 150)
@Data
public class SmsAuthNum implements Serializable {

    @Id
    private String userPhoneNum;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdDateTime;

    private String authNum;

    public SmsAuthNum(String userPhoneNum, String authNum) {
        this.userPhoneNum = userPhoneNum;
        this.authNum = authNum;
        this.createdDateTime = LocalDateTime.now();
    }

}
