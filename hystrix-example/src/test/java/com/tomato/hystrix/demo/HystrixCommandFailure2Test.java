package com.tomato.hystrix.demo;

import com.netflix.hystrix.HystrixEventType;
import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo2;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 多级降级（备用方案）测试
 *
 * HystrixCommand1执行失败后，在fallback1的执行中嵌入HystrixCommand2
 *
 */
public class HystrixCommandFailure2Test {

    /**
     * execute()
     */
    @Test
    public void testExecute() throws InterruptedException {
        HystrixCommandDemo2 command = new HystrixCommandDemo2("First Fallback", HystrixEventType.TIMEOUT);

        String result = command.execute();
        System.out.println(result);

        TimeUnit.MILLISECONDS.sleep(2000);
    }
}
