package com.tomato.hystrix.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import rx.Observable;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * HystrixCommand的四个执行方法和降级处理
 */
public class HystrixCommandDemo1 extends HystrixCommand<String> {

    private String name;
    private HystrixEventType hystrixEventType;

    public HystrixCommandDemo1(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
        this.hystrixEventType = HystrixEventType.SUCCESS;
    }

    public HystrixCommandDemo1(String name, HystrixEventType hystrixEventType) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
        this.hystrixEventType = hystrixEventType;
    }

    @Override
    protected String run() {//此处执行依赖服务的调用
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute run()...");

        switch(hystrixEventType) {
            case BAD_REQUEST:
                throw new HystrixBadRequestException(name + " is bad request");
            case TIMEOUT:
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                    System.out.println("thread " + t.getName() + ": now " + name + " execute timeout test...");
                } catch (InterruptedException e) {
                    throw new RuntimeException(name + " is time out");
                }
            case FAILURE:
                throw new RuntimeException(name + " command always fails");
            default:
                return "Hello " + name + "!";
        }
    }

    @Override
    protected String getFallback() {//run()失败后，执行降级逻辑
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute getFallback()...");
        //throw new RuntimeException(name + " test exception");
        return "the result from getFallback() of " + name;
    }

    @Override
    public String execute() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute execute()...");
        return super.execute();
    }

    @Override
    public Future<String> queue() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute queue()...");
        return super.queue();
    }

    @Override
    public Observable<String> observe() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute observe()...");
        return super.observe();
    }

    @Override
    public Observable<String> toObservable() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute toObservable()...");
        return super.toObservable();
    }
}
