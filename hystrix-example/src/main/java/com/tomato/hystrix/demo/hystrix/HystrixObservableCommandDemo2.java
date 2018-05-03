package com.tomato.hystrix.demo.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class HystrixObservableCommandDemo2 extends HystrixObservableCommand<String> {

    private final String name;
    private HystrixEventType hystrixEventType;

    public HystrixObservableCommandDemo2(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
        this.hystrixEventType = HystrixEventType.SUCCESS;
    }

    public HystrixObservableCommandDemo2(String name, HystrixEventType hystrixEventType) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
        this.hystrixEventType = hystrixEventType;
    }

    @Override
    protected Observable<String> construct() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute construct()...");

        switch(hystrixEventType) {
            case BAD_REQUEST:
                throw new HystrixBadRequestException(name + " is bad request");
            case TIMEOUT:
                throw new RuntimeException(name + " is time out");
            case FAILURE:
                throw new RuntimeException(name + " command always fails");
            default:
                return Observable.just("a", "b", "c").concatWith(Observable.<String> error(new RuntimeException("forced error")))
                                .doOnNext(new Action1<String>() {

                                    @Override
                                    public void call(String v) {
                                        System.out.println("thread " + Thread.currentThread().getName() + ": now " + v + " execute call()...");
                                    }
                                })
                                .subscribeOn(Schedulers.computation());

        }
    }

    @Override
    protected Observable resumeWithFallback() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute resumeWithFallback()...");
        return Observable.just("resumeWithFallback execute success");
    }

    @Override
    public Observable observe() {
        return super.observe();
    }

    @Override
    public Observable toObservable() {
        return super.toObservable();
    }
}
