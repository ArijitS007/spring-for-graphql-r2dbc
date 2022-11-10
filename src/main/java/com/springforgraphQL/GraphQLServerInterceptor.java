package com.springforgraphQL;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

// Intercepts the calls coming to graphQL
// can be used for Authentication, Authorization, Logging etc.
@Component
@Slf4j
public class GraphQLServerInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        log.info("Interceptor Logs: {}", request.getDocument());
        return chain.next(request);
    }


}
