package com.tomato.hystrix.demo.hystrix;


import com.netflix.hystrix.*;
import com.netflix.hystrix.exception.HystrixBadRequestException;

import java.util.concurrent.TimeUnit;

/**
 * 断容器 —— 信号量隔离
 */
public class HystrixCommandDemo5 extends HystrixCommand<String> {

    private String name;
    private HystrixEventType hystrixEventType;

    public HystrixCommandDemo5(String name, HystrixEventType hystrixEventType) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("testGroup"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("testKey"))
                        //.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("testThreadPool"))
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter()
                                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)//隔离策略，设置为信号量隔离，线程池配置将无效
                        )
        );

        this.name = name;
        this.hystrixEventType = hystrixEventType;
    }

    @Override
    protected String getFallback() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute getFallback()...");

        //throw new RuntimeException(name + " test exception");
        return "the result from getFallback() of " + name;
    }

    @Override
    protected String run() throws Exception {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute run()...");

        switch(hystrixEventType) {
            case BAD_REQUEST:
                throw new HystrixBadRequestException(name + " is bad request");
            case TIMEOUT:
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(name + " is time out");
                }
            case FAILURE:
                throw new RuntimeException(name + " command always fails");
            default:
                return "Hello " + name + "!";
        }
    }
}
