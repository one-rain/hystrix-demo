package com.tomato.hystrix.demo;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixEventType;
import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo5;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HystrixCommandDemo5Test {

    /**
     * 1、信号隔离是对客户端请求线程的并发限制，采用信号隔离时，hystrix的线程相关配置将无效
     * 2、当请求并发量大于hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests时，请求执行fallback
     * 3、当fallback的并发线程数大于hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests时，fallback将抛异常fallback execution rejected
     *
     * @throws Exception
     */
    @Test
    public void testIsolation1() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests", 4);//信号量最多允许执行run的并发数
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests", 2);//信号量最多允许执行fallback的并发数

        try {
            for(int i = 0; i < 6; i++) {
                final int j = i;

                Thread thread = new Thread(new Runnable() {//模拟并发

                    @Override
                    public void run() {
                        new HystrixCommandDemo5("hello " + j, HystrixEventType.SUCCESS).execute();
                    }
                });
                thread.start();
            }
        } catch(Exception e) {
            e.printStackTrace();
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
