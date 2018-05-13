import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import static org.junit.Assert.assertEquals;

public class FactorialServiceTest {

    @Test
    public void testFactorial() {
        Flux<Double> factorialGenerator = new FactorialService().generateFactorial(10);
        factorialGenerator
                .doOnNext(t -> System.out.println(t))
                .last()
                .subscribe(t -> assertEquals(3628800.0, t, 0.0));
    }

}
