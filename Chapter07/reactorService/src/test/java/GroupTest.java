import io.netty.handler.ssl.OptionalSslHandler;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class GroupTest {

    @Test
    public  void testGrouping(){
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
        fibonacciGenerator.take(20)
                .groupBy(i -> {
                    List<Integer> divisors= Arrays.asList(2,3,5,7);
                    Optional<Integer> divisor = divisors.stream().filter(d -> i % d == 0).findFirst();
                    return divisor.map(x -> "Divisible by "+x).orElse("Others");

                })
                 .concatMap(x -> {
                     System.out.println("\n"+x.key());
                     return x;
                 })
                .subscribe(x -> System.out.print(" "+x));
    }

}