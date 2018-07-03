package com.sample.web;

import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.nio.charset.Charset;
import java.util.Optional;

import static org.springframework.web.reactive.function.BodyInserters.fromDataBuffers;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

@Configuration
class FibonacciConfigurer {
    @Bean
    RouterFunction<ServerResponse> functionaEndpoint() {
        HandlerFunction<ServerResponse> helloWorld = request -> {
            Optional<String> name = request.queryParam("name");
            Publisher data = Flux.just("Hello to ", name.orElse("the world."));
            return ServerResponse.ok().body(fromPublisher(data, String.class));
        };

        RouterFunction<ServerResponse> helloWorldRoute =
                RouterFunctions.route(RequestPredicates.path("/hello"), helloWorld)
                        .filter((request, next) -> ServerResponse.status(HttpStatus.BAD_REQUEST).build());

        return helloWorldRoute;

    }

    @Bean
    RouterFunction<ServerResponse> fibonacciEndpoint() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        RouterFunction<ServerResponse> fibonacciRoute =
                RouterFunctions.route(RequestPredicates.path("/fibonacci"),
                        request -> ServerResponse.ok().body(fromPublisher(fibonacciGenerator, Long.class)));
        return fibonacciRoute;
    }
}
