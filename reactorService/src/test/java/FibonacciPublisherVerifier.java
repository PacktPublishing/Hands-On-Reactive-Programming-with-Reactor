import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

public class FibonacciPublisherVerifier extends PublisherVerification<Integer> {

    public FibonacciPublisherVerifier(){
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
        return null;
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        return new FibonacciPublisher();
    }
}
