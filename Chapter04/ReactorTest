import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.*;
import reactor.util.function.Tuples;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class ReactorTest {
    @Test
    public void test1() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator.take(10).subscribe(t -> System.out.println(t),
                e -> e.printStackTrace(),
                () -> System.out.println("Finished"),
                s -> s.request(100));
    }

    @Test
    public void test4() {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        DirectProcessor<Long> data = DirectProcessor.create();
    }

    @Test
    public void testProcessor() {
        DirectProcessor<Long> data = DirectProcessor.create();
        data.take(2).subscribe(t -> System.out.println(t));
        data.onNext(10L);
        data.onNext(11L);
        data.onNext(12L);
    }


    @Test
    public void testDirectProcessor() {
        DirectProcessor<Long> data = DirectProcessor.create();
        data.take(2).subscribe(t -> System.out.println(t),
                e -> e.printStackTrace(),
                () -> System.out.println("Finished 1"));
        data.onNext(10L);
        data.onComplete();
        data.subscribe(t -> System.out.println(t),
                e -> e.printStackTrace(),
                () -> System.out.println("Finished 2"));
        data.onNext(12L);
    }

    @Test
    public void testDirectProcessor2() {
        DirectProcessor<Long> data = DirectProcessor.create();
        data.subscribe(t -> System.out.println(t),
                e -> e.printStackTrace(),
                () -> System.out.println("Finished"),
                s -> s.request(1));
        data.onNext(10L);
        data.onNext(11L);
        data.onNext(12L);
    }

    @Test
    public void testUnicastProcessor() throws InterruptedException {
        DirectProcessor<Long> data = DirectProcessor.create();
        data.subscribe(t -> {
            System.out.println(t);
        });
        FluxSink<Long> sink = data.sink();
        Thread thread1 = new Thread(() -> {
            int count = 0;
            while (count < 100) {
                sink.next(10L);
                count++;
            }
        });
        Thread thread2 = new Thread(() -> {
            int count = 0;
            while (count < 100) {
                sink.next(20L);
                count++;
            }
        });
        thread1.start();
        thread2.start();
        Thread.sleep(10000);

    }

    @Test
    public void testEmitterProcessor() {
        EmitterProcessor<Long> data = EmitterProcessor.create(1);
        FluxSink<Long> sink = data.sink();
        data.subscribe(t -> System.out.println(t));
        sink.next(10L);
        sink.next(11L);
        sink.next(12L);
        data.subscribe(t -> System.out.println(t));
        sink.next(13L);
        sink.next(14L);
        sink.next(15L);
    }


    @Test
    public void testReplayProcessor() {
        ReplayProcessor<Long> data = ReplayProcessor.create(3);
        data.subscribe(t -> System.out.println(t));
        data.onNext(10L);
        data.onNext(11L);
        data.onNext(12L);
        data.onNext(13L);
        data.onNext(14L);
        data.subscribe(t -> System.out.println(t));
    }

    @Test
    public void testTopicProcessor() {
        TopicProcessor<Long> data = TopicProcessor.<Long>builder()
                .executor(Executors.newFixedThreadPool(2)).build();
        data.subscribe(t -> System.out.println(t));
        data.subscribe(t -> System.out.println(t));
        FluxSink<Long> sink = data.sink();
        sink.next(10L);
        sink.next(11L);
        sink.next(12L);
    }

    @Test
    public void testWorkQueueProcessor() {
        WorkQueueProcessor<Long> data = WorkQueueProcessor.<Long>builder().build();
        data.subscribe(t -> System.out.println("1. " + t));
        data.subscribe(t -> System.out.println("2. " + t));
        FluxSink<Long> sink = data.sink();
        sink.next(10L);
        sink.next(11L);
        sink.next(12L);
    }

    @Test
    public void testHotPublisher() throws Exception {
        final UnicastProcessor<Long> hotSource = UnicastProcessor.create();
        final Flux<Long> hotFlux = hotSource.publish().autoConnect();
        hotFlux.take(5).subscribe(t -> System.out.println("1. " + t));
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            int c1 = 0, c2 = 1;
            while (c1 < 1000) {
                hotSource.onNext(Long.valueOf(c1));
                int sum = c1 + c2;
                c1 = c2;
                c2 = sum;
                if (c1 == 144) {
                    hotFlux.subscribe(t -> System.out.println("2. " + t));
                }
            }
            hotSource.onComplete();
            latch.countDown();
        }).start();
        latch.await();
    }

    @Test
    public void testColdPublisher() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        fibonacciGenerator.take(5).subscribe(t -> System.out.println("1. " + t));
        fibonacciGenerator.take(5).subscribe(t -> System.out.println("2. " + t));

    }
}
