package com.tomato.hystrix.demo;

import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo6;
import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HystrixCommandDemo6Test {

    /**
     * 相邻两个请求可以自动合并的前提是两者足够近：启动执行的间隔时间足够小，默认10ms
     *
     * @throws Exception
     */
    @Test
    public void testCollapser() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            Future<String> f1 = new HystrixCommandDemo6(1).queue();
            Future<String> f2 = new HystrixCommandDemo6(2).queue();
            Future<String> f3 = new HystrixCommandDemo6(3).queue();

            //修改睡眠的时间，会改变合并命令的数量，默认启动执行的间隔时间小于10ms的command会合并成一个
            TimeUnit.MILLISECONDS.sleep(13);

            Future<String> f4 = new HystrixCommandDemo6(4).queue();
            Future<String> f5 = new HystrixCommandDemo6(5).queue();
            Future<String> f6 = new HystrixCommandDemo6(6).queue();

            System.out.println(f1.get());
            System.out.println(f2.get());
            System.out.println(f3.get());
            System.out.println(f4.get());
            System.out.println(f5.get());
            System.out.println(f6.get());

            //numExecuted表示共有几个命令执行，1个批量多command请求算一个
            int numExecuted = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size();

            System.err.println("num executed: " + numExecuted);
            //System.err.println("HystrixRequestLog.getCurrentRequest().getAllExecutedCommands(): " + HystrixRequestLog.getCurrentRequest().getAllExecutedCommands());

            int numLogs = 0;
            for (HystrixInvokableInfo<?> command : HystrixRequestLog.getCurrentRequest().getAllExecutedCommands()) {
                numLogs++;

                // assert the command is the one we're expecting
                //assertEquals("testKey", command.getCommandKey().name());

                System.err.println(command.getCommandKey().name() + " => command.getExecutionEvents(): " + command.getExecutionEvents());

                // confirm that it was a COLLAPSED command execution
                assertTrue(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
                assertTrue(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
            }

            assertEquals(numExecuted, numLogs);
        } finally {
            context.shutdown();
        }
    }
}
