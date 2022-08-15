package com.monkeypenthouse.core.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

public class AwsConfiguration {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${cloudwatch.accessKey}")
    private String accessKey;

    @Value("${cloudwatch.secretKey}")
    private String secretKey;

    private AWSCredentials awsCredentials;

    @PostConstruct
    public void init() {
        awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new AWSStaticCredentialsProvider(awsCredentials);
    }
}
