import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class BroadcastTest {

    @Test
    public void testReplayBroadcast() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
               sink.complete();
            else
                sink.next(state.getT1());
            System.out.println("generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        Flux<Long> braodcastGenerator=fibonacciGenerator.doFinally(x -> {
            System.out.println("Closing ");
        }).replay().autoConnect(2);


        fibonacciGenerator.subscribe(x -> System.out.println("[Fib] 1st : "+x));
        fibonacciGenerator.subscribe(x -> System.out.println("[Fib] 2nd : "+x));

        braodcastGenerator.subscribe(x -> System.out.println("1st : "+x));
        braodcastGenerator.subscribe(x -> System.out.println("2nd : "+x));
      }

    @Test
    public void testBroadcastAdditionalSubscribers() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            System.out.println("generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        Flux<Long> broadcastGenerator=fibonacciGenerator.take(20)
                .delayElements(Duration.ofMillis(100))
                .doFinally(x -> System.out.println("Closing "))
                .replay().refCount(2);

        Disposable disposable=broadcastGenerator.subscribe();
        broadcastGenerator.subscribe(x -> {
            System.out.println("1st : " + x);
            disposable.dispose();
        });

        Thread.sleep(200);

      //  broadcastGenerator.subscribe(x -> System.out.println("2nd : "+x));

        Thread.sleep(4000);

      }

    @Test
    public void testBroadcastWithCancel() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            System.out.println("generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator=fibonacciGenerator.doFinally(x -> {
            System.out.println("Closing ");
        }).replay().refCount(2);

        fibonacciGenerator.subscribe(new BaseSubscriber<Long>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
            }

            @Override
            protected void hookOnNext(Long value) {
                System.out.println("1st: "+value);
                cancel();
            }
        });

        fibonacciGenerator.subscribe(new BaseSubscriber<Long>() {
            @Override
            protected void hookOnNext(Long value) {
                System.out.println("2nd : "+value);
                cancel();
            }
        });
        Thread.sleep(500);

    }

    @Test
    public void testPublishBroadcast() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            System.out.println("generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator=fibonacciGenerator.doFinally(x -> {
            System.out.println("Closing ");
        }).publish().autoConnect(2);

        fibonacciGenerator.subscribe(new BaseSubscriber<Long>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
            }

            @Override
            protected void hookOnNext(Long value) {
                System.out.println("1st: "+value);
            }
        });

        fibonacciGenerator.subscribe(x -> System.out.println("2nd : "+x));
        Thread.sleep(500);

    }
}