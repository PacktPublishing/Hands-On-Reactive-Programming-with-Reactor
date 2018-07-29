package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.util.function.Tuples;

import java.time.Duration;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;


@SpringBootApplication
@Configuration
@ComponentScan("com.sample.web")
public class ReactorMain {
    public static void main(String[] args) {
        SpringApplication.run(ReactorMain.class, args);
        readFibonacciNumbers();
    }

    public static void readFibonacciNumbers() {
        WebClient client = WebClient.create("http://localhost:8080");
        Flux<Long> result = client.get()
                .uri("/fibonacci")
                .retrieve()
                .bodyToFlux(Long.class).limitRequest(10L);
        result.subscribe( x-> System.out.println(x));
    }
}
