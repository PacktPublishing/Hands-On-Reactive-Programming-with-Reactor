import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class FibonacciPublisher implements Publisher<Integer> {

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        int count = 0, a = 0, b = 1;
        while (count < 50) {
            int sum = a + b;
            subscriber.onNext(b);
            a = b;
            b = sum;
            count++;
        }

        subscriber.onComplete();
    }
}
