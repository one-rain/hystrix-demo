package com.tomato.hystrix.demo.hystrix;


import com.netflix.hystrix.*;
import com.netflix.hystrix.exception.HystrixBadRequestException;

import java.util.concurrent.TimeUnit;

/**
 * 断路器 —— 线程池隔离（默认策略）
 */
public class HystrixCommandDemo4 extends HystrixCommand<String> {

    private String name;
    private HystrixEventType hystrixEventType;

    public HystrixCommandDemo4(String name, HystrixEventType hystrixEventType) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("testGroup"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("testKey"))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("testThreadPool"))
                        /*
                        .andThreadPoolPropertiesDefaults(
                                HystrixThreadPoolProperties.Setter()
                                        .withCoreSize(4)//核心线程池大小，尽可能保持池的小
                                        .withMaximumSize(10)//限制最大线程池大小，设置了allowMaximumSizeToDivergeFromCoreSize，此值才生效
                                        //.withAllowMaximumSizeToDivergeFromCoreSize(true)
                                        .withMaxQueueSize(10)//BlockingQueue的最大队列大小
                        )
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter()
                                        //.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)//隔离策略，默认为线程隔离
                                        //.withCircuitBreakerEnabled(true)//是否打开断容器，默认为打开
                                        .withCircuitBreakerRequestVolumeThreshold(20)//滚动窗口中将使电路跳闸的最小请求数
                                        .withCircuitBreakerErrorThresholdPercentage(80)
                                        .withCircuitBreakerErrorThresholdPercentage(50)//错误百分比，等于或高于该错误百分比时，将请求短路至回退逻辑，默认值为50
                                		//.withCircuitBreakerForceOpen(false)// 默认为false，置为true时，所有请求都将被拒绝，直接到fallback
                		                //.withCircuitBreakerForceClosed(true)// 默认为false，置为true时，不管错误百分比如何，都将允许请求
                                        //.withExecutionTimeoutEnabled(true)//run()是否有超时限制，默认为有
                		                .withExecutionTimeoutInMilliseconds(500)//超时时间，默认值1000，单位毫秒
                                        //.withMetricsRollingStatisticalWindowInMilliseconds(10000)//统计滚动窗口的持续时间，单位毫秒，默认为10s
                                        //.withMetricsRollingPercentileWindowInMilliseconds(60000)//保持执行时间的滚动窗口的持续时间
                        )*/

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
                    TimeUnit.MILLISECONDS.sleep(1000);
                    //System.out.println("thread " + t.getName() + ": now " + name + " execute timeout test...");
                    return "Hello " + name + " execute time gt 600ms";
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
