import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.util.LinkedList;
import java.util.List;

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
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        List<Long> fibonacciSeries = new LinkedList<>();
        int size = 50;
        fibonacciGenerator.take(size).subscribe(t -> {
            fibonacciSeries.add(t);
        });
        System.out.println(fibonacciSeries);
        assertEquals( 7778742049L, fibonacciSeries.get(size-1).longValue());
    }

}
