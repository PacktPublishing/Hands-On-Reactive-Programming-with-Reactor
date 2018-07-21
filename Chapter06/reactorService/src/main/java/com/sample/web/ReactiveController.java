package com.sample.web;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;

@Controller
public class ReactiveController {
    private static final ObjectMapper om = new ObjectMapper();

    @GetMapping("/")
    public String handle(Model model) {
        model.addAttribute("text", "Hello WebFlux");
        return "home.html";
    }

    @GetMapping("/freemarker")
    public String handleFreemarker(Model model) {
        model.addAttribute("text", "Hello WebFlux");
        return "home";
    }

    @GetMapping("/thymeleaf")
    public String handleThymleaf(Model model) {
        model.addAttribute("text", "Hello WebFlux");
        return "home";
    }

    @GetMapping("/text")
    @ResponseBody
    public Publisher<String> handler() {
        return Flux.just("Hello world!", "This is from webflux");
    }

    @GetMapping("/numbers")
    public String handleSeries(Model model) {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        Mono<List<Long>> listMono = fibonacciGenerator.collectList();
        model.addAttribute("series",listMono);
        return "numbers";
    }



}
