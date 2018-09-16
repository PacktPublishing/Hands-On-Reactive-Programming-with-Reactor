import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;
import reactor.util.function.Tuples;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ReactorProbe {

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


        @Test
    public void testPublisherStub() throws Exception {
        Hooks.onOperatorDebug();
        TestPublisher<Long> numberGenerator= TestPublisher.<Long>create();
        StringWriter out = new StringWriter();
        new PrintService().printEventNumbers(numberGenerator.flux(),new PrintWriter(out));
        numberGenerator.next(1L,2L,3L,4L);
        numberGenerator.complete();
        assertTrue(out.getBuffer().length() >0);
    }

    @Test
    public void testNonComplientPublisherStub() throws Exception {
        TestPublisher<Long> numberGenerator= TestPublisher.createNoncompliant(TestPublisher.Violation.REQUEST_OVERFLOW);
        StepVerifier.create(numberGenerator, 1L)
                .then(() -> numberGenerator.emit(1L,2L,3L,4L))
                .expectNext(1L)
                .verifyError();

    }


}

class PrintService{
    public void printEventNumbers(Flux<Long> source, PrintWriter writer) {
        source
                .filter(x -> x % 2 == 0)
                .subscribe(new BaseSubscriber<Long>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Long value) {
                        request(1);
                    }

                });
    }
}