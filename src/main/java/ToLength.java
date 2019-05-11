import rx.Observable;

public class ToLength implements Observable.Transformer<String, Integer> {

    public static ToLength toLength(){
        return new ToLength();
    }

    private ToLength(){
        super();
    }

    @Override
    public Observable<Integer> call(Observable<String> stringObservable) {
        return stringObservable.map(String::length);
    }
}
