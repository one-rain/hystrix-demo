package com.tomato.hystrix.demo;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixEventType;
import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo4;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class HystrixCommandDemo4Test {

    /**
     * 测试断路器被打开
     * 通过调整circuitBreaker.requestVolumeThreshold和circuitBreaker.errorThresholdPercentage两个参数测试
     *
     * 同时满足以下条件，断路器将打开：
     * 1、整个链路请求数达到阀值（`circuitBreaker.requestVolumeThreshold`）时，满足第一个条件。默认情况下，10秒内请求数超过20次
     * 2、在满足第一个条件的前提下，如果请求的错误数占比大于阀值（`circuitBreaker.errorThresholdPercentage`），则会打开断路器，默认为50%
     *
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.coreSize", 2);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.metrics.rollingStats.timeInMilliseconds", 10000);//窗口
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.requestVolumeThreshold", 16);//最小请求次数阀值
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.errorThresholdPercentage", 50);//错误百分比
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 500);//run()超时时间

        long t = System.currentTimeMillis();

        for (int i = 0; i < 20; i++) {
            try {
                HystrixCommandDemo4 commandDemo = new HystrixCommandDemo4("hell " + i, ((i % 3 == 0) ? HystrixEventType.TIMEOUT : HystrixEventType.SUCCESS));
                commandDemo.execute();

                //System.out.println(commandDemo.getProperties().circuitBreakerRequestVolumeThreshold().get());

                if (commandDemo.isCircuitBreakerOpen()) {
                    System.out.println(System.currentTimeMillis() - t + " - 断路器被打开，执行第 " + (i + 1) + " 个command");
                }
            } catch (Exception e) {
                System.out.println("run()抛出Exception时，被捕获到这里" + e.getCause());
            }
        }

        TimeUnit.MILLISECONDS.sleep(2000);

        System.out.println("-------");

        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();

        for (Map.Entry<Thread, StackTraceElement[]> entry : sort(map)) {
            System.out.println(entry.getKey().getName());
        }
    }

    /**
     * 测试断路器关闭后的休眠
     *
     * 1、断路器被打开后，根据circuitBreaker.sleepWindowInMilliseconds设置，会休眠一段时间，这段时间内的所有请求，都直接fallback
     * 2、休眠时间过后，Hystrix会将断路器状态改为半开状态，然后尝试性的执行一次command，如果成功，则关闭断路器，如果失败，继续保持打开状态
     * 3、断路器打开后，断路器的健康检查指标会重置，重新开始计算
     *
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.metrics.rollingStats.timeInMilliseconds", 10000);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.requestVolumeThreshold", 3);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.errorThresholdPercentage", 20);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds", 3000);//断路器关闭后，休眠时间

        for (int i = 0; i < 10; i++) {
            try {
                HystrixCommandDemo4 commandDemo = new HystrixCommandDemo4("hell " + i, ((i % 3 == 0) ? HystrixEventType.TIMEOUT : HystrixEventType.SUCCESS));
                commandDemo.execute();

                HystrixCommandMetrics.HealthCounts hc = commandDemo.getMetrics().getHealthCounts();
                System.out.println("断路器状态：" + commandDemo.isCircuitBreakerOpen() + ", 请求总数：" + hc.getTotalRequests());

                if (commandDemo.isCircuitBreakerOpen()) {
                    System.out.println("断路器被打开，执行第 " + (i + 1) + " 个command");
                    //休眠4秒
                    TimeUnit.MILLISECONDS.sleep(1500);
                }

                TimeUnit.MILLISECONDS.sleep(500);
            } catch (Exception e) {
                System.out.println("run()抛出Exception时，被捕获到这里" + e.getCause());
            }
        }

    }

    /**
     * 设置断路器参数
     *
     * 1、如果hystrix.command.default.circuitBreaker.enabled设置为false，将不使用断路器来跟踪健康状况，也不会在断路器跳闸时将其短路（即不会执行fallback）
     * 2、如果hystrix.command.default.circuitBreaker.forceOpen设置为true，断路器将强制打开，所有请求将被拒绝，直接进入fallback
     * 3、如果hystrix.command.default.circuitBreaker.forceClosed设置为true，断路器将强制关闭，无论错误百分比如何，都将允许请求（永远会执行run）。
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.requestVolumeThreshold", 6);//最小请求次数阀值
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.errorThresholdPercentage", 30);//错误百分比
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 500);//run()超时时间
        //ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.enabled", false);
        //ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.forceOpen", true);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.forceClosed", true);


        for (int i = 0; i < 10; i++) {
            try {
                HystrixCommandDemo4 commandDemo = new HystrixCommandDemo4("hell " + i, ((i % 2 == 0) ? HystrixEventType.TIMEOUT : HystrixEventType.SUCCESS));
                commandDemo.execute();

                HystrixCommandMetrics.HealthCounts hc = commandDemo.getMetrics().getHealthCounts();
                System.out.println("断路器状态：" + commandDemo.isCircuitBreakerOpen() + ", 请求总数：" + hc.getTotalRequests());

                TimeUnit.MILLISECONDS.sleep(500);
            }catch (Exception e) {
                System.out.println("run()抛出Exception时，被捕获到这里" + e.getCause());
            }
        }
        TimeUnit.MILLISECONDS.sleep(2000);

        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();

        for (Map.Entry<Thread, StackTraceElement[]> entry : sort(map)) {
            System.out.println(entry.getKey().getName());
        }
    }

    private List<Map.Entry<Thread, StackTraceElement[]>> sort(Map<Thread, StackTraceElement[]> map) {
        List<Map.Entry<Thread, StackTraceElement[]>> list = new ArrayList<Map.Entry<Thread, StackTraceElement[]>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Thread, StackTraceElement[]>>() {
            public int compare(Map.Entry<Thread, StackTraceElement[]> o1, Map.Entry<Thread, StackTraceElement[]> o2) {
                return (o1.getKey().getName()).compareTo(o2.getKey().getName());
            }
        });
        return list;
    }
}
