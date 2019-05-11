import com.sun.media.jfxmediaimpl.MediaDisposer;
import rx.*;
import rx.functions.*;
import rx.internal.operators.UnicastSubject;
import rx.observables.ConnectableObservable;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RxJavaTutorial {

    public static void main(String[] args) {
        System.out.println("Hello wordl");

        zipWithExample();
    }

    private static void ziphDifferentValueExample(){
        Observable.zip(Observable.just(1,2,3), Observable.just(4,5,6,7),(item1, item2) -> {
            return "Item 1 " + item1 + " item2 " + item2;
        }).toBlocking()
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }
                });

    }

    private static void zipWithExample(){
        Observable.interval(1, TimeUnit.SECONDS)
                .take(1)
                .zipWith(Observable.interval(3, TimeUnit.SECONDS), new Func2<Long, Long, String>() {
                    @Override
                    public String call(Long aLong, Long aLong2) {
                        return "Source1 " + aLong + " Source2 " + aLong2;
                    }
                })
                .doOnUnsubscribe(System.out::println)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("On Complete");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String aLong) {
                        System.out.println("On Next : " + aLong);
                    }
                });

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Observable.Operator<String, String> cleanStringFn = subscriber -> new Subscriber<String>() {
        @Override
        public void onCompleted() {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }

        @Override
        public void onError(Throwable e) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onError(e);
            }
        }

        @Override
        public void onNext(String s) {
            if (!subscriber.isUnsubscribed()) {
                String result = s.replaceAll("[^A-Za-z0-9]", "");
                subscriber.onNext(result);
            }
        }
    };

    private static void customOperatorExample(){
        List<String> list = Arrays.asList("john_1", "tom-3");
        List<String> results = new ArrayList<>();

                Observable.from(list)
                        .lift(cleanStringFn)
                        .compose(ToLength.toLength())
                        .toBlocking()
                        .subscribe(System.out::println);

    }

    private static void skippingElementBackPressure(){
        PublishSubject.create().range(1,1000000).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void usingOperator(){

        String[] result = {""};
        Observable.using(
                new Func0<String>() {
                    @Override
                    public String call() {
                        return "Hellow";
                    }
                },
                new Func1<String, Observable<?>>() {
                    @Override
                    public Observable<?> call(String s) {
                        return Observable.just("A", "B");
                    }
                },
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("Disposed");
                    }
                }
        ).subscribe(
                v -> result[0] += v,
                e -> result[0] += e
        );

        System.out.println(result[0]);
    }


    private static void exampleJavaOperator(){
        String[] stringArray = {"Barbara", "James", "Mary", "John",
                "Patricia", "Robert", "Michael", "Linda"};
        Arrays.sort(stringArray,String::compareToIgnoreCase);

        for (String k: stringArray)
         System.out.println(k);
    }

    private static void connectableObservablesExample(){
        String[] result = {""};
        ConnectableObservable<Long> connectableObservable = ConnectableObservable.interval(200, TimeUnit.MILLISECONDS)
                .publish();

        connectableObservable.subscribe(i -> result[0] += i);

        connectableObservable.connect();

        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(result[0]);
    }

    private static void blockingRxExample(){

        Observable.interval(2, TimeUnit.SECONDS)
                .scan(new StringBuilder(), StringBuilder::append).toBlocking()
                .subscribe(new Observer<StringBuilder>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(StringBuilder stringBuilder) {
                        System.out.println(stringBuilder.toString());
                    }
                });

    }

    private static void completableObservableExample(){
        Observable.just(1)
                .toSingle().toCompletable()
                .subscribe(new Completable.CompletableSubscriber() {
            @Override
            public void onCompleted() {
                System.out.println("Parse On Complete success");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Masuk On Error parse");
            }

            @Override
            public void onSubscribe(Subscription d) {

            }
        });


        // Completable to continue the task use andThen Operator

        Observable.just(1)
                .toSingle().toCompletable().andThen(Observable.just(4,5))
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .toBlocking()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("Observable 2 " + integer);
                    }
                });
    }

    private static void singleTypeObservable(){
        Single.just(1)
                .subscribe(new SingleSubscriber<Integer>() {
                    @Override
                    public void onSuccess(Integer value) {
                        System.out.println("On Single : " + value);
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });

        Observable<Integer> intObs = Observable.just(1);
    }

    private static void timeStampExample(){
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(3)
                .timestamp()
                .observeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Observer<Timestamped<Long>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Timestamped<Long> longTimestamped) {
                        System.out.println(longTimestamped);
                    }
                });
    }

    private static void timeOutExample(){
        Observable.just(1l,2l,3l,4l,5l)
                .delay(2,TimeUnit.SECONDS)
                .timeout(500,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        System.out.println("onError: ");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println("onNext: " + aLong);
                    }
                });
    }

    private static void timeIntervalExample(){
//        Observable.interval(100, TimeUnit.MILLISECONDS)
//                .take(3)
//                .timeInterval()
//                .subscribe(new Subject<Long>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//
//                    }
//
//                    @Override
//                    public boolean hasObservers() {
//                        return false;
//                    }
//                });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void materializeAndDemeterializeExample(){
        Observable.just("A", "B", "C", "D", "E", "F")
                .materialize()
                .subscribe(new Observer<Notification<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Notification<String> stringNotification) {
                        System.out.println(stringNotification.getKind() + " value: " +stringNotification.getValue());
                    }
                });
    }

    private static void doExample(){
        Observable.just(1,2,3,4,5)
                .doOnNext(kata -> System.out.println("doOnNext: " + kata))
                .subscribe();

        Observable.just(1,2,3,4,5)
                .doOnEach(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Complete is called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext: " + integer);
                    }
                })
                .doOnSubscribe(() -> System.out.println("On SubScribe"))
                .doOnUnsubscribe(() -> System.out.println("OnUnSubsribe"))
                .subscribe();
    }
    private static Subscription sub;
    private static void delayExample(){
        sub = Observable.just("A", "B", "C", "D", "E", "F")
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("OnNext: " + s);

                        if (s.equalsIgnoreCase("E")){
                            sub.unsubscribe();
                        }
                    }
                });
    }

    private static void compositeDisposible(){
//        Observable.just(1,2,3,4)
//                .subscribeOn(Schedulers.trampoline())
//                .subscribe
    }

    private static void withLatestFrom(){
        Subject subject = PublishSubject.create();
        Subject publishSubject = PublishSubject.create();

        subject.onNext("a");

        Observable o = publishSubject.withLatestFrom(subject, new Func2() {
            @Override
            public Object call(Object o, Object o2) {
                return (String) o + (String) o2;
            }
        });

        o.subscribe(it -> {
            System.out.println("onNext: " + it);
        }, err -> {}, ()-> System.out.println("onComplete"));

        publishSubject.onNext("c");
        subject.onNext("d");
        publishSubject.onNext("e");
        publishSubject.onNext("f");
        publishSubject.onNext("g");
        subject.onNext("h");
        publishSubject.onNext("i");
    }

    private static void hotUnicastSubject(){
        UnicastSubject<Integer> unicastSubject = UnicastSubject.create();

        unicastSubject.onNext(0);

        unicastSubject.subscribe(it -> {
            System.out.println("Observer 1 onNext: " + it);
        }, err ->{}, () -> System.out.println("Observer 1 onComplete"));

        unicastSubject.onNext(1);
        unicastSubject.onNext(2);

        unicastSubject.subscribe(it -> {
            System.out.println("Observer 2 onNext: "+ it);
        }, err -> System.out.println("Observer 2 onError"), () -> System.out.println("Observer 2 onCompleter"));

        unicastSubject.onNext(3);
        unicastSubject.onCompleted();
    }

    private static void hotReplaySubjectExample(){
        ReplaySubject<Integer> replaySubject = ReplaySubject.create();

        replaySubject.onNext(0);

        replaySubject.subscribe(it ->{
            System.out.println("Observer 1 onNext: " + it);
        }, (Throwable onError) ->{}, () -> System.out.println("Observer 1 onSubscribe"));

        replaySubject.onNext(1);
        replaySubject.onNext(2);

        replaySubject.subscribe(it -> {
            System.out.println("Observer 2 onNext: " + it);
        }, err -> {}, () -> System.out.println("Observer 2 onCompleted"));

        replaySubject.onNext(3);

        replaySubject.subscribe(it ->{
            System.out.println("Observer 3 oNNExt: " + it);
        }, err ->{}, () ->System.out.println("Observer 3 onCompleted"));

        replaySubject.onNext(4);
        replaySubject.onCompleted();
    }

    private static void hotBehaviorSubjectExample(){
        BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create();

        behaviorSubject.onNext(0);

        behaviorSubject.subscribe(it ->{
            System.out.println("Observer 1 onNext: " + it);
        }, (Throwable onError) -> {}, () -> System.out.println("Observer 1 onCompleted"));

        behaviorSubject.onNext(1);
        behaviorSubject.onNext(2);

        behaviorSubject.subscribe(it ->{
            System.out.println("Observer 2 onNext: " + it);
        }, er -> {}, () -> System.out.println("Observer 2 onCompleted"));

        behaviorSubject.onNext(3);

        behaviorSubject.subscribe(it ->{
            System.out.println("Observer 3 onNext: " + it);
        }, er -> {}, () -> System.out.println("Observer 3 onCompleted"));

        behaviorSubject.onNext(4);
        behaviorSubject.onCompleted();
    }

    private static void hoPublishSubjectExample(){
        PublishSubject<Integer>  pSubject= PublishSubject.create();

        pSubject.onNext(0);

        pSubject.subscribe(it ->{
            System.out.println("Observer 1 onNext: " + it);
        },onError -> {}, () -> System.out.println("Observer 1 onCompleted"));

        pSubject.onNext(1);
        pSubject.onNext(2);

        pSubject.subscribe(it -> {
            System.out.println("Observer 2 oNNext: " + it);
        },(Throwable throwable) -> {}, () -> System.out.println("Observed 2 onCompleted"));

        pSubject.onNext(3);

        pSubject.subscribe(it -> {
            System.out.println("Observer 3 onNext: " + it);
        }, er -> {}, () -> System.out.println("Observer 3 onCompleted"));

        pSubject.onNext(4);
        pSubject.onCompleted();
    }


    private static void switchOnNext(){
        Observable.switchOnNext(Observable.interval(600, TimeUnit.MILLISECONDS)
        .map(aLong -> {
             return Observable.interval(590, TimeUnit.MILLISECONDS);}
            )
        )
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(item -> System.out.println("onNext: " + item));
    }

    private static void takeUntilExample(){
        Observable.just(1,2,3,5,6)
                .takeUntil(id -> id == 5)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("onNext: " + integer);
                    }
                });
    }

    private static void zipExample(){
        Observable<String> alphabets1 = Observable.interval(2,TimeUnit.SECONDS)
                .map(id-> "A" + id);

        Observable<String> alphabets2 = Observable.interval(1,TimeUnit.SECONDS)
                .map(id-> "B" + id);

        Observable.zip(alphabets1,alphabets2,
                (alpha1,alpha2) -> alpha1 + " " + alpha2)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext: " + s);
                    }
                });
    }

    private static void mergeExample(){
        Observable<String> alphabets1 = Observable.interval(2, TimeUnit.SECONDS)
                .map(id -> "A" + id);

        Observable<String> alphabets2 = Observable.interval(1, TimeUnit.SECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return "B" + aLong;
                    }
                });

        Observable<String> alphabets3 = Observable.interval(3,TimeUnit.SECONDS)
                .map(id-> "C" + id);

        Observable.merge(alphabets1,alphabets2,alphabets3)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext: " + s);
                    }
                });
    }

    private static void joinExample(){
        Observable<Long> left = Observable.interval(100, TimeUnit.MILLISECONDS);
        Observable<Long> right = Observable.interval(100,TimeUnit.MILLISECONDS);

        left.join(right
                , new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.timer(2, TimeUnit.SECONDS);
                    }
                }, new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.timer(0, TimeUnit.SECONDS);
                    }
                }, new Func2<Long, Long, Long>() {
                    @Override
                    public Long call(Long aLong, Long aLong2) {
                        System.out.println("Left result: " + aLong + " Right Result: " + aLong2);
                        return aLong+aLong2;
                    }
                })
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                System.out.println("onNext: "+aLong);
            }
        });
    }

    private static void combineLatestExample2(){
        Observable<Integer> obser1 = Observable.interval(500,TimeUnit.MILLISECONDS).just(1,2,3,4);
        Observable<Integer> obser2 = Observable.interval(500, TimeUnit.MILLISECONDS).just(5,6,7);


        List<Observable<Integer>> observablCombine = Arrays.asList(obser1,obser2);

        Observable.combineLatest(observablCombine, new FuncN<String>() {
            @Override
            public String call(Object... args) {
                int concat = 0;
                for (Object values : args) {
                    if (values instanceof Integer)
                         concat += (Integer) values;
                }
                return String.valueOf(concat);
            }
        }).subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .subscribe(item -> System.out.println(item));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void takeLastExample(){
        Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
                .takeLast(4)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext: " + s);
                    }
                });
    }

    private static void takeExample(){
        Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
                .take(4)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext: " + s);
                    }
                });
    }

    private static void skipLeastExample(){
        Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
                .skipLast(4)
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext: "+ s);
                    }
                });
    }

    private static void skipExample(){
        Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
                .skip(6)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext: " + s);
                    }
                });
    }

    private static void simpleExample(){
        Observable timedObservable = Observable.just(1,2,3,4,5,6)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .zipWith(Observable.interval(
                        1,TimeUnit.MILLISECONDS),(item, time) ->item);

        timedObservable
                .sample(2,TimeUnit.SECONDS)
                .subscribe(new Observer() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println("onNext: "+ o);
                    }
                });
    }

    private static void ignoreElements(){
        Observable.range(1, 10)
                .ignoreElements()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Complete");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }
                });
    }

    private static void elementAtExample(){
        Observable.just(1,2,3,4,5)
                .elementAt(2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }

    private static void distinctUntilChanged(){
        Observable.just(1,2,3,3,2,1)
                .distinctUntilChanged()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }

    private static void distinctExample(){
        Observable.just(1,2,1,3,2)
                .distinct()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }

    private static void reduceExample(){
        Observable.just(1,2,3,4,5,6)
                .reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(integer);
            }
        });
    }

    private static void combineLatestExample(){
        Observable.combineLatest(Observable.just("Aku "), Observable.just("Hebat"), new Func2<String, String, String>() {
            @Override
            public String call(String s, String s2) {
                return s + s2;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }
        });
    }

    private static void scanExample(){
        Observable.just(1,2,3,4,5)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer+integer2;
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(integer);
            }
        });
    }

    private static void concatEample(){
        Observable.concat(Observable.just(1,1,2), Observable.just(3,2))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }

    private static void defaultEmpty(){
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                subscriber.onCompleted();
            }
        }).observeOn(Schedulers.from(Executors.newFixedThreadPool(1)))
                .defaultIfEmpty(new String("Gak ada isinya"))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {
                        System.out.println(o);
                    }
                });
    }

    private static void scanOperator(){
        Observable.just("Aku","Kamu","dia")
                .scan(new Func2<String, String, String>() {
                    @Override
                    public String call(String s, String s2) {
                        return s+s2;
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }
                });
    }

    private static void groupByOperatoor(){
        Observable.range(3,10)
                .groupBy(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return (integer % 2 == 0) ? true : false;
                    }
                })
                .subscribe(new Observer<GroupedObservable<Boolean, Integer>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GroupedObservable<Boolean, Integer> booleanIntegerGroupedObservable) {
                        System.out.println(booleanIntegerGroupedObservable.getKey());
                        if (booleanIntegerGroupedObservable.getKey()){
                        booleanIntegerGroupedObservable.subscribe(new Observer<Integer>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Integer integer) {
                                System.out.println("On Next Genap " + integer);
                            }
                        });
                    }else if (!booleanIntegerGroupedObservable.getKey()){
                            booleanIntegerGroupedObservable.subscribe(new Subscriber<Integer>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Integer integer) {
                                    System.out.println("On Next Ganjil " + integer);
                                }
                            });
                        }
                    }
                });
    }

    private static void flatMapOperator(){
        Observable.just(1,2,3,4)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }
                });

//        Thread.sleep(3000);
    }

    private static void mapOperator(){
        Observable.just(1,2,3,4)
                .map(new Func1<Integer, Integer>() {

                    @Override
                    public Integer call(Integer integer) {
                        return integer + integer;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(integer);
                    }
                });
    }


        private static void bufferOperator(){
        Observable.just(1,2,3)
                .buffer(2)
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        System.out.println("OnNext : ");
                        for (Integer i : integers)
                        System.out.println(i);
                    }
                });
    }

    private static void rangeOperator(){
        List<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(2);
        data.add(3);

        Observable.range(data.get(1),2)
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("Data " + integer);
            }
        });
    }

    private static void cobaTimer(){
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        System.out.println("onNext: " + aLong);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private static void cobaIntervalRx() {
        Observable
                .just(1)
                .timer(2, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println(aLong.toString());
                    }
                });
    }

    private static void observabeleJust(){
        Observable.just(new String[]{"satu","dua","tiga"})
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String[] strings) {
                        System.out.println(Arrays.toString(strings));
                    }
                });
    }

    private static void simpleRXJust() {
        Observable.just(1, 2, 3, 4)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer + 1;
                    }
                })
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(2)))
                .subscribe(new Action1<Integer>() {

                    @Override
                    public void call(Integer integer) {
                        System.out.println("On Next " + integer);
                    }
                }, new Action1<Throwable>() {

                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("On Error");
                    }
                }, new Action0() {

                    @Override
                    public void call() {
                        System.out.println("On Complete");
                    }
                });
    }
}
