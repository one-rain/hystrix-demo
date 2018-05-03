package com.tomato.hystrix.demo;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo7;
import org.junit.Test;


/**
 * 1、cache只有同在一个context中才生效
 *
 */
public class HystrixCommandDemo7Test {

    @Test
    public void testWithoutCacheHits() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            HystrixCommandDemo7 command0 = new HystrixCommandDemo7(0,"hello");
            HystrixCommandDemo7 command1 = new HystrixCommandDemo7(1,"hello");
            HystrixCommandDemo7 command2 = new HystrixCommandDemo7(2,"hello");
            HystrixCommandDemo7 command3 = new HystrixCommandDemo7(58672,"hello");

            boolean result0 = command0.execute();
            System.out.println("result0: " + result0 + ", isResponseFromCache: " + command0.isResponseFromCache());

            boolean result1 = command1.execute();
            System.out.println("result1: " + result1 + ", isResponseFromCache: " + command1.isResponseFromCache());

            boolean result2 = command2.execute();
            System.out.println("result2: " + result2 + ", isResponseFromCache: " + command2.isResponseFromCache());

            boolean result3 = command3.execute();
            System.out.println("result3: " + result3 + ", isResponseFromCache: " + command3.isResponseFromCache());
        } finally {
            context.shutdown();
        }
    }

    @Test
    public void testWithCacheHits() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            HystrixCommandDemo7 command2a = new HystrixCommandDemo7(2, "hello");
            HystrixCommandDemo7 command2b = new HystrixCommandDemo7(2, "hello");

            boolean result0 = command2a.execute();
            System.out.println("result0: " + result0 + ", isResponseFromCache: " + command2a.isResponseFromCache());

            boolean result1 = command2b.execute();
            System.out.println("result1: " + result1 + ", isResponseFromCache: " + command2b.isResponseFromCache());
        } finally {
            context.shutdown();
        }

        // start a new request context
        context = HystrixRequestContext.initializeContext();
        try {
            HystrixCommandDemo7 command2c = new HystrixCommandDemo7(2, "hello");

            boolean result2 = command2c.execute();
            System.out.println("result2: " + result2 + ", isResponseFromCache: " + command2c.isResponseFromCache());
        } finally {
            context.shutdown();
        }
    }
}
