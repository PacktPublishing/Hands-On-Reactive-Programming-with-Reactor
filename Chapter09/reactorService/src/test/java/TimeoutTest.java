import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class TimeoutTest {

    @Test
    public void testTimeout() throws  Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                throw new RuntimeException("Value out of bounds");
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        fibonacciGenerator
                .delayElements(Duration.ofSeconds(1))
                .timeout(Duration.ofMillis(500))
                .subscribe(System.out::println, e -> {
                    System.out.println(e);
                    countDownLatch.countDown();
                });
        countDownLatch.await();
    }

    @Test
    public void testTimeoutWithFallback() throws  Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                throw new RuntimeException("Value out of bounds");
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        fibonacciGenerator
                .delayElements(Duration.ofSeconds(1))
                .timeout(Duration.ofMillis(500),Flux.just(-1L))
                .subscribe(e -> {
                    System.out.println("Received :"+e);
                    countDownLatch.countDown();
                });
        countDownLatch.await();
    }

    @Test
    public void testRetry() throws  Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                throw new RuntimeException("Value out of bounds");
            else
                sink.next(state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        fibonacciGenerator
                 .retry(1)
                .subscribe(System.out::println, e -> {
                    System.out.println("received :"+e);
                    countDownLatch.countDown();
                },countDownLatch::countDown);
        countDownLatch.await();
    }
    
}