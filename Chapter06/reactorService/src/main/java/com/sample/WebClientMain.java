package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class WebClientMain {

    public void readFibonacciNumbers() {
        WebClient client = WebClient.create("http://localhost:8080");
        Flux<Long> result = client.get()
                .uri("/fibonacci").accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Long.class);

        result.subscribe( x-> System.out.println(x));
    }

    public void readFibonacciNumbersUsingExchange() {
        WebClient client = WebClient.create("http://localhost:8080");
        Flux<Long> result = client.get()
                .uri("/fibonacci").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMapMany(response -> response.bodyToFlux(Long.class));

        result.subscribe( x-> System.out.println(x));
    }


}
