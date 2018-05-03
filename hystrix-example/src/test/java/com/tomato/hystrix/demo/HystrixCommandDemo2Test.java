package com.tomato.hystrix.demo;

import com.netflix.hystrix.HystrixEventType;
import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo2;
import org.junit.Test;

/**
 * 多次降级就是在getFallback()中调用另一个HystrixCommand，当然，也可以是其它的服务
 */
public class HystrixCommandDemo2Test {

    @Test
    public void testExecute() throws Exception {
        HystrixCommandDemo2 command1 = new HystrixCommandDemo2("World", HystrixEventType.TIMEOUT);

        String result1 = command1.execute();
        System.out.println("result: " + result1);
    }
}
