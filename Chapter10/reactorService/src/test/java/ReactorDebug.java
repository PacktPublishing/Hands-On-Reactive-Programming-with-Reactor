import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;
import reactor.util.function.Tuples;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

public class ReactorDebug {

      @Test
    public void testPublisherStub() throws Exception {
          Flux<Long> fibonacciGenerator = getFibonacciGenerator().log();
        StringWriter out = new StringWriter();
        new PrintService().printEventNumbers(fibonacciGenerator,new PrintWriter(out));

        assertTrue(out.getBuffer().length() >0);
    }

    private Flux<Long> getFibonacciGenerator() {
        return Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                throw new IllegalStateException();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });
    }


}