package com.tomato.hystrix.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import org.apache.commons.lang.StringUtils;


/**
 * Command Name、Command Group和Command Thread-Pool
 */
public class HystrixCommandDemo3 extends HystrixCommand<String> {

    private String name;

    public HystrixCommandDemo3(String name, String groupKey, String commandKey) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey)));
        this.name = name;
    }

    public HystrixCommandDemo3(String name, String groupKey, String commandKey, String threadPool) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(threadPool)));
        this.name = name;
    }

    @Override
    protected String run() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute run()...");
        return "Hello " + name + "!";
    }

    @Override
    protected String getFallback() {
        Thread t = Thread.currentThread();
        System.out.println("thread " + t.getName() + ": now " + name + " execute getFallback()...");
        //throw new RuntimeException(name + " test exception");
        return new HystrixCommandDemo1("Second Fallback").execute(); //调用另外一个HystrixCommand
    }
}
