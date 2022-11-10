package com.springforgraphQL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;

@Configuration
public class HttpGraphQLClientConfig {

    // The app exposes two endpoint one for HTTP communication and the other for websocket
    // This is for HTTP
    @Bean
    public HttpGraphQlClient httpGraphQLClient(){
        return HttpGraphQlClient.builder().url("http://localhost:8080/graphql").build();
    }
}
