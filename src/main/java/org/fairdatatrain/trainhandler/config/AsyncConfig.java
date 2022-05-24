package org.fairdatatrain.trainhandler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {

    // TODO: custom config properties
    @Value("${poll.timeout:10000}")
    private final Long pollTimeout = 10000L;

    @Bean("pollTimeout")
    public Long getPollTimeout() {
        return pollTimeout;
    }
}
