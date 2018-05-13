import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ReactorTest {
    @Test
    public void testSimpleStringFlux() {
        StringBuilder str = new StringBuilder();
        Flux<String> stingFlux = Flux.just("Quick", "brown", "fox", "jumped", "over", "the", "wall");
        stingFlux.subscribe(t -> {
            str.append(t).append(" ");
        });
        assertEquals("Quick brown fox jumped over the wall ", str.toString());
    }

    @Test
    public void testFibonacci() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    System.out.println("generated " + state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        List<Long> fibonacciSeries = new LinkedList<>();
        int size = 50;
        fibonacciGenerator.take(size).subscribe(t -> {
            System.out.println("consuming " + t);
            fibonacciSeries.add(t);
        });
        System.out.println(fibonacciSeries);
        assertEquals(7778742049L, fibonacciSeries.get(size - 1).longValue());
    }


    @Test
    public void testFibonacciFluxSink() {
        Flux<Long> fibonacciGenerator = Flux.create(e -> {
            long current = 1, prev = 0;
            AtomicBoolean stop = new AtomicBoolean(false);
            e.onCancel(() -> {
                stop.set(true);
                System.out.println("******* Stop Received ****** ");
            });
            while (current > 0) {
                e.next(current);
                System.out.println("generated " + current);
                long next = current + prev;
                prev = current;
                current = next;
            }
            e.complete();

        }, FluxSink.OverflowStrategy.IGNORE);
        List<Long> fibonacciSeries = new LinkedList<>();
        System.out.println(fibonacciSeries);
    }


}
