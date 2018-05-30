import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ReactorTest {
    @Test
    public void testFibonacciFlter() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    if (state.getT1() < 0) {
                        sink.complete();
                    } else {
                        sink.next(state.getT1());
                    }
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        fibonacciGenerator.filterWhen(a -> Mono.just(a < 10)).subscribe(t -> {
            System.out.println(t);
        });

        fibonacciGenerator.filter(a -> a % 2 == 0).subscribe(t -> {
            System.out.println(t);
        });
    }

    @Test
    public void testFibonacciSkip() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    if (state.getT1() < 0) {
                        sink.error(new RuntimeException("Negative number found"));
                    } else {
                        sink.next(state.getT1());
                    }
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });

        fibonacciGenerator.skip(10).subscribe(t -> {
            System.out.println(t);
        });
        fibonacciGenerator.skip(Duration.ofMillis(10)).subscribe(t -> {
            System.out.println(t);
        });
        fibonacciGenerator.skipUntil(t -> (t > 100)).subscribe(t -> {
            System.out.println(t);
        });

    }

    @Test
    public void testFibonacciTake() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    if (state.getT1() < 0) {
                        sink.error(new RuntimeException("Negative number found"));
                    } else {
                        sink.next(state.getT1());
                    }
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });

        fibonacciGenerator.take(Duration.ofSeconds(5)).subscribe(t -> {
            System.out.println(t);
        });


    }

    @Test
    public void testFibonacciMap() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        RomanNumber numberConvertor = new RomanNumber();
        fibonacciGenerator.skip(1).take(10).map(t -> numberConvertor.toRoman(t.intValue())).subscribe(t -> {
            System.out.println(t);
        });

    }

    @Test
    public void testFibonacciFactorization() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        Factorization numberConvertor = new Factorization();
        fibonacciGenerator.skip(1).take(10).flatMap(t -> Flux.fromIterable(numberConvertor.findfactor(t.intValue()))).subscribe(t -> {
            System.out.println(t);
        });

    }

    @Test
    public void testFibonacciRepeat() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        AtomicInteger integer = new AtomicInteger(0);
        fibonacciGenerator.take(10).repeat(() -> {
            return integer.getAndAdd(1) < 2;
        }).subscribe(t -> {
            System.out.println(t);
        });

    }


    @Test
    public void testFibonacciCollect() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        fibonacciGenerator.take(10).collectMultimap(t -> t % 2 == 0 ? "even" : "odd").subscribe(t -> {
            System.out.println(t);
        });
    }

    @Test
    public void testFibonacciLogicalOperator() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        fibonacciGenerator.take(10).all(x -> x > 0).subscribe(t -> {
            System.out.println(t);
        });
    }

    @Test
    public void testFibonacciConcat() {
        Flux<Long> fibonacciGenerator = Flux.generate(
                () -> Tuples.<Long, Long>of(0L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT1() + state.getT2());
                });
        fibonacciGenerator.take(10).concatWith(Flux.just(new Long[]{-1L, -2L, -3L, -4L})).subscribe(t -> {
            System.out.println(t);
        });

    }
}

class Factorization {
    Collection<Integer> findfactor(int number) {
        ArrayList<Integer> factors = new ArrayList<>();
        for (int i = 1; i <= number; i++) {
            if (number % i == 0) {
                factors.add(i);
            }
        }
        return factors;
    }
}

class RomanNumber {

    TreeMap<Integer, String> map = new TreeMap<>();

    RomanNumber() {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    String toRoman(int number) {
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number - l);
    }

}