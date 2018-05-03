package com.tomato.hystrix.demo;

import com.netflix.hystrix.HystrixEventType;
import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo1;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 1）HystrixCommand执行run()抛出异常时，会调用getFallback()执行降级逻辑（HystrixBadRequestException除外）
 * 2）对于observe()，run()执行完成后（如果抛出异常，等getFallback()执行完），执行subscribe()
 * 3）subscribe()中，先执行onNext()，如果执行成功，则接着执行onCompleted()，否则，执行onError()
 * 4）如果getFallback()执行抛出异常，会导致command直接停止，无法执行subscribe()，所以，需要注意捕捉异常
 */
public class HystrixCommandFailure1Test {

    /**
     * execute()
     */
    @Test
    public void testExecute() throws InterruptedException {
        HystrixCommandDemo1 command1 = new HystrixCommandDemo1("World", HystrixEventType.TIMEOUT);
        HystrixCommandDemo1 command2 = new HystrixCommandDemo1("Bod", HystrixEventType.TIMEOUT);

        String result1 = command1.execute();
        String result2 = command2.execute();

        System.out.println(result1);
        System.out.println(result2);

        TimeUnit.MILLISECONDS.sleep(2000);
    }

    /**
     * queue()
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testQueue() throws ExecutionException, InterruptedException {
        HystrixCommandDemo1 command = new HystrixCommandDemo1("World", HystrixEventType.TIMEOUT);

        Future<String> future = command.queue();
        System.out.println(future.get());
    }

    /**
     * observe()
     *
     * @throws Exception
     */
    @Test
    public void testObservable() throws Exception {
        Observable<String> fWorld = new HystrixCommandDemo1("World", HystrixEventType.TIMEOUT).observe();
        Observable<String> fBob = new HystrixCommandDemo1("Bob", HystrixEventType.TIMEOUT).observe();

        //blocking
        String result1 = fWorld.toBlocking().single();
        //assertEquals("Hello World!", result1);
        System.out.println(result1);

        String result2 = fBob.toBlocking().single();
        //assertEquals("Hello Bob!", result2);
        System.out.println(result2);

        //以下为2种订阅方式
        //non-blocking
        fWorld.subscribe(new Observer<String>() {//执行生命周期事件

            @Override
            public void onError(Throwable e) {//onNext()执行中抛出异常时，执行该方法
                System.out.println("now execute onError(), error message: " + e.getMessage());
            }

            @Override
            public void onNext(String v) { //command执行完，执行该方法, v为command的执行结果
                System.out.println("now execute onNext(), result: " + v);
                //throw new RuntimeException("onNext() throw exception");
            }

            @Override
            public void onCompleted() {//onNext()执行成功完成后，执行该方法
                System.out.println("now execute onCompleted()");
                //throw new RuntimeException("onCompleted() throw exception");
            }

        });

        //non-blocking
        fBob.subscribe(new Action1<String>() {//只执行回调事件
            @Override
            public void call(String v) {
                System.out.println("now execute call(), result: " + v);
            }
        });
    }
}
