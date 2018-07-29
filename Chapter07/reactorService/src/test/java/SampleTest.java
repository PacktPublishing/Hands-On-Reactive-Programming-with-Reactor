import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class SampleTest {

    @Test
    public  void testSample() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        CountDownLatch latch = new CountDownLatch(1);
        fibonacciGenerator
                .delayElements(Duration.ofMillis(100L))
                .sample(Duration.ofSeconds(1))
                .subscribe(x -> System.out.println(x), e -> latch.countDown() , () -> latch.countDown());
        latch.await();
    }

    @Test
    public  void testSampleFirst() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        CountDownLatch latch = new CountDownLatch(1);
        fibonacciGenerator
                .delayElements(Duration.ofMillis(100L))
                .sampleFirst(Duration.ofSeconds(1))
                .subscribe(x -> System.out.println(x), e -> latch.countDown() , () -> latch.countDown());
        latch.await();
    }

}