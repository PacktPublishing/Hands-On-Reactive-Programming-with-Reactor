import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.ArrayList;

public class ReactorTest2 {

    @Test
    public void testPublisherProbe() throws Exception {
        Flux<Long> fibonacciGenerator = Flux.generate(() -> Tuples.<Long,
                Long>of(0L, 1L), (state, sink) -> {
            if (state.getT1() < 0)
                sink.complete();
            else
                sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT1() + state.getT2());
        });

        PublisherProbe<Long> publisherProbe = PublisherProbe.of(fibonacciGenerator);
        publisherProbe.flux().subscribe();

        publisherProbe.assertWasSubscribed();
        publisherProbe.assertWasRequested();

    }




}