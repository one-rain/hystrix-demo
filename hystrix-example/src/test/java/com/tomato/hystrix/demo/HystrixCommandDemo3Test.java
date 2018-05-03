package com.tomato.hystrix.demo;

import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo3;
import org.junit.Test;

import java.util.*;

/**
 * 1、CommandKey/CommandName是一个依赖服务的command标识
 * 2、GroupKey是将报告，警报，仪表板或团队/库所有权等命令组合在一起
 * 3、ThreadPoolKey用于监视，度量标准发布，缓存和其他此类用途的HystrixThreadPool
 * 4、没有定义ThreadPoolKey时，ThreadPoolKey使用GroupKey，定义了ThreadPoolKey时，则使用定义值（采用线程策略隔离的情况下）
 * 5、command在执行run()时，会创建一个线程，该线程的名称是ThreadPoolKey和序列号的组合，序列号是该线程在线程池中的创建顺序
 * 6、使用ThreadPoolKey的原因是多个command可能属于同一个所有权或逻辑功能“组”，但某些command又需要彼此隔离。
 */
public class HystrixCommandDemo3Test {

    @Test
    public void testGroup() throws Exception {
        HystrixCommandDemo3 command1 = new HystrixCommandDemo3("world", "testGroup-A", "key-world");
        HystrixCommandDemo3 command2 = new HystrixCommandDemo3("bob", "testGroup-B", "key-bob");
        HystrixCommandDemo3 command3 = new HystrixCommandDemo3("tom", "testGroup-B", "key-tom");

        String result1 = command1.execute();
        String result2 = command2.execute();
        String result3 = command3.execute();

        System.out.println("1 result: " + result1);
        System.out.println("2 result: " + result2);
        System.out.println("3 result: " + result3);

        System.out.println("-------");

        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : sort(map)) {
            System.out.println(entry.getKey().getName());
        }
    }

    @Test
    public void testThread() throws Exception {
        HystrixCommandDemo3 command1 = new HystrixCommandDemo3("world", "testGroup-A", "key-world", "testThreadPool-A");
        HystrixCommandDemo3 command2 = new HystrixCommandDemo3("bob", "testGroup-A", "key-bob", "testThreadPool-A");
        HystrixCommandDemo3 command3 = new HystrixCommandDemo3("tom", "testGroup-B", "key-tom", "testThreadPool-B");
        HystrixCommandDemo3 command4 = new HystrixCommandDemo3("fob", "testGroup-B", "key-fob", "testThreadPool-C");

        String result1 = command1.execute();
        String result2 = command2.execute();
        String result3 = command3.execute();
        String result4 = command4.execute();

        System.out.println("1 result: " + result1);
        System.out.println("2 result: " + result2);
        System.out.println("3 result: " + result3);
        System.out.println("4 result: " + result4);

        System.out.println("-------");

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
