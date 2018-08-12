import org.junit.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;

public class ErrorHandlingTest {

    @Test
    public void testThrownException() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
              //  sink.complete();
                throw new RuntimeException("Value out of bounds");
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .doFinally(x -> {
                    System.out.println("Finally block started");
                })
                .subscribe(System.out::println);
    }

    @Test
    public void testErrorRaised() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new RuntimeException("Value out of bounds"));
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .doOnTerminate( () -> {
                    System.out.println("Terminalted");
                })
                .subscribe(System.out::println);
    }

    @Test
    public void testDoError() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new RuntimeException("Value out of bounds"));
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .doOnError(System.out::println)
                .subscribe(System.out::println, e -> e.printStackTrace());
    }


    @Test
    public void testDoTerminate() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new RuntimeException("Value out of bounds"));
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .doAfterTerminate(() -> System.out.println("Terminated"))
                .subscribe(System.out::println);
    }
    @Test
    public void testDoFinally() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new RuntimeException("Value out of bounds"));
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .doFinally( x -> System.out.println("invoking finally"))
                .subscribe(System.out::println, e -> e.printStackTrace());
    }


    @Test
    public void testErrorReturn() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new IllegalStateException("Value out of bounds"));
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .onErrorReturn(IllegalStateException.class,-1L)
                .onErrorReturn(RuntimeException.class,0L)
                .subscribe(System.out::println);
    }

    @Test
    public void testErrorMap() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new RuntimeException("Value out of bounds"));
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .onErrorMap(x -> new IllegalStateException("Publisher threw error", x))
                .subscribe(System.out::println,System.out::println);
    }

    @Test
    public void testErrorResume() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new RuntimeException("Value out of bounds"));
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .onErrorResume(x -> Flux.just(0L,-1L,-2L))
                .subscribe(System.out::println);
    }


    void raiseCheckedException() throws IOException {
        throw new IOException("Raising checked Exception");
    }

    @Test
    public void testUsingMethod() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        Closeable closable = () -> System.out.println("closing the stream");
        Flux.using(() -> closable, x -> fibonacciGenerator, e -> {
            try {
                e.close();
            } catch (Exception e1) {
                throw Exceptions.propagate(e1);
            }
        }).subscribe(System.out::println);
    }

}