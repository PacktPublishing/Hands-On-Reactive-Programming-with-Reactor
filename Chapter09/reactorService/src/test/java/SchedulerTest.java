import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SchedulerTest {

    @Test
    public void testReactorThread() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
               sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                })
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "))
                .subscribe(x -> print("Sub received : "+x));
    }

    @Test
    public void testReactorDelayThread() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                }).delayElements(Duration.ZERO)
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "))
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(500);
    }


    @Test
    public void testImmediateSchedular() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.interval(Duration.ofMillis(10),Schedulers.immediate());
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                })
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "))
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(500);
    }

    @Test
    public void testSingleScheduler() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                }).delayElements(Duration.ZERO,Schedulers.single())
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "+x))
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(500);
    }

    @Test
    public void testSingleSchedulerBlockingOps() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                }).delayElements(Duration.ZERO,Schedulers.single())
                .window(10)
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "+x))
                .subscribe(x -> print("Sub received : "+x.blockFirst()));
        Thread.sleep(500);
    }

    @Test
    public void testParallelScheduler() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                }).delayElements(Duration.ZERO,Schedulers.parallel())
                 .doOnNext(x -> print("Next value is  "+ x))
                .doFinally(x -> print("Closing "+x))
                .subscribe(x -> print("Sub received : "));
        Thread.sleep(500);
    }


    @Test
    public void testElasticScheduler() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                }).delayElements(Duration.ZERO,Schedulers.elastic())
                .window(10)
                .doOnNext(x -> print("Next value is  "+ x))
                .doFinally(x -> print("Closing "+x))
                .subscribe(x -> print("Sub received : "+x.blockFirst()));
        Thread.sleep(500);
    }

    @Test
    public void testExecutorScheduler() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        fibonacciGenerator
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                }).delayElements(Duration.ZERO,Schedulers.fromExecutor(executor))
                 .doOnNext(x -> print("Next value is  "+ x))
                .doFinally(x -> print("Closing "+executor.isShutdown()))
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(5000);
        print("Is shutdown ? "+executor.isShutdown());
    }

    @Test
    public void testReactorPublishOn() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .publishOn(Schedulers.single())
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                })
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "))
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(500);
    }


    @Test
    public void testReactorSubscribeOn() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                 .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                })
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "))
                .subscribeOn(Schedulers.single())
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(500);
    }

    @Test
    public void testReactorComposite() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .publishOn(Schedulers.single())
             //   .delayElements(Duration.ZERO)
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                })
                .doOnNext(x -> print("Next value is  "+x))
                .doFinally(x -> print("Closing "))
                .subscribeOn(Schedulers.single())
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(500);
    }


    @Test
    public void testParalleFlux() throws Exception{
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            print("Generating next of "+ state.getT2());

            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator
                .parallel()
                .runOn(Schedulers.parallel())
                .filter(x -> {
                    print("Executing Filter");
                    return x < 100;
                })
                .doOnNext(x -> print("Next value is  "+x))
                .sequential()
                .doFinally(x -> print("Closing "))
                .subscribeOn(Schedulers.single())
                .subscribe(x -> print("Sub received : "+x));
        Thread.sleep(500);
    }

    static void print(String text){
        System.out.println("["+Thread.currentThread().getName()+"] "+text);
    }



}