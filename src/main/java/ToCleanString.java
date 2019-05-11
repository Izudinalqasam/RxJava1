import rx.Observable;
import rx.Subscriber;

public class ToCleanString implements Observable.Operator<String,String> {

    public static ToCleanString toCleanString(){
        return new ToCleanString();
    }

    private ToCleanString(){
        super();
    }

    @Override
    public Subscriber<? super String> call(Subscriber<? super String> subscriber) {
        return new Subscriber<String>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()){
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!subscriber.isUnsubscribed()){
                    subscriber.onError(e);
                }

            }

            @Override
            public void onNext(String s) {
                if (!subscriber.isUnsubscribed()){
                    final String result = s.replaceAll("[^A-Za-z0-9]","");
                    subscriber.onNext(result);
                }
            }
        };
    }
}
