import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class FibonacciSubscriber implements Subscriber<Long> {
    private Subscription sub;

    @Override
    public void onSubscribe(Subscription s) {
        sub = s;
        sub.request(10);
    }

    @Override
    public void onNext(Long fibNumber) {
        System.out.println(fibNumber);
        sub.cancel();
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        sub=null;
    }

    @Override
    public void onComplete() {
        System.out.println("Finished");
        sub=null;
    }
}
