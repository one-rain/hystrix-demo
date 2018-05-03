package com.tomato.hystrix.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.exception.HystrixBadRequestException;

import java.util.concurrent.TimeUnit;

/**
 * 多次降级
 */
public class HystrixCommandDemo2 extends HystrixCommand<String> {

    private final String name;
    private HystrixEventType hystrixEventType;

    public HystrixCommandDemo2(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
        this.hystrixEventType = HystrixEventType.SUCCESS;
    }

    public HystrixCommandDemo2(String name, HystrixEventType hystrixEventType) {
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
        return new HystrixCommandDemo1("Second Fallback").execute(); //调用另外一个HystrixCommand
    }
}
