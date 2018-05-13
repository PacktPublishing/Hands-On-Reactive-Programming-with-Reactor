import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;
import reactor.util.function.Tuples;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FactorialService {

    Flux<Double> generateFactorial(long number) {
        Flux<Double> factorialStream = Flux.generate(
                () -> Tuples.<Long, Double>of(0L, 1.0d),
                (state, sink) -> {
                    Long factNumber = state.getT1();
                    Double factValue = state.getT2();
                    if (factNumber <= number)
                        sink.next(factValue);
                    else
                        sink.complete();
                    return Tuples.of(factNumber + 1, (factNumber + 1) * factValue);
                });
        return factorialStream;
    }

}
