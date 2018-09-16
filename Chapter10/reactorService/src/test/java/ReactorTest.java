import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

public class ReactorTest {

    @Test
    public void testExpectation() throws Exception {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            System.out.println("generating next of " + state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        StepVerifier.create(fibonacciGenerator.take(10))
                .expectNext(0L, 1L, 1L)
                .expectNextCount(7)
                .verifyComplete();

        // Expect error for validating errors
    }

    @Test
    public void testErrorExpectation() throws Exception {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() > 30)
                sink.error(new IllegalStateException("Value out of bound"));
            else
                sink.next(state.getT1());
            System.out.println("generating next of " + state.getT1());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        StepVerifier.create(fibonacciGenerator.take(10))
                .expectNext(0L, 1L, 1L)
                .expectNextCount(6)
                .expectErrorSatisfies(x -> {
                    assert(x instanceof IllegalStateException);
                })
                .verify();

        // Expect error for validating errors
    }

    @Test
    public void testConsumeWhile() throws Exception {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            System.out.println("generating next of " + state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });

        StepVerifier.create(fibonacciGenerator)
                .thenConsumeWhile(x -> x >= 0)
                .verifyComplete();

    }


    @Test
    public void testErrorFallback() throws Exception {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.error(new RuntimeException("Value out of Bounds"));
            else
                sink.next(state.getT1());
            System.out.println("generating next of " + state.getT2());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });

        StepVerifier.create(fibonacciGenerator, Long.MAX_VALUE)
                .thenConsumeWhile(x -> x >= 0)
                .verifyError();

        StepVerifier.create(fibonacciGenerator.onErrorReturn(1L), Long.MAX_VALUE)
                .thenConsumeWhile(x -> x >= 0)
                .verifyComplete();

    }


    @Test
    public void testRecordWith() throws Exception {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            System.out.println("generating next of " + state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });

        StepVerifier.create(fibonacciGenerator, Long.MAX_VALUE)
                .recordWith(() -> new ArrayList<>())
                .thenConsumeWhile(x -> x >= 0)
                .expectRecordedMatches(x -> x.size() > 0)
                .expectComplete()
                .verify();
    }


    @Test
    public void testBackPressure() throws Exception {
        Flux<Integer> numberGenerator = Flux.create(x -> {
            System.out.println("Requested Events :" + x.requestedFromDownstream());
            int number = 1;
            while (number < 100) {
                x.next(number);
                number++;
            }
            x.complete();
        }, FluxSink.OverflowStrategy.ERROR);

        StepVerifier.create(numberGenerator, 1L)
                .thenConsumeWhile(x -> x >= 0)
                .expectError()
                .verifyThenAssertThat()
                .hasDroppedElements();
    }


    @Test
    public void testDelay() {
        StepVerifier.withVirtualTime(() -> Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .delaySequence(Duration.ofMillis(100)))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(100))
                .expectNextCount(9)
                .verifyComplete();
    }


}