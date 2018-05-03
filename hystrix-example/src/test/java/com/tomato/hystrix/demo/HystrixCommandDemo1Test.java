package com.tomato.hystrix.demo;

import com.tomato.hystrix.demo.hystrix.HystrixCommandDemo1;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * HystrixCommand的执行流程：
 * 1）execute()是同步堵塞的，它调用了queue().get()方法，execute()执行完后，会创建一个新线程运行run()
 * 2）queue()是异步非堵塞的，它调用了toObservable().toBlocking().toFuture()方法，queue()执行完后，会创建一个新线程运行run()。Future.get()是堵塞的，它等待run()运行完才返回结果
 * 3）observe()是异步的，是热响应调用，它调用了toObservable().subscribe(subject)方法，observe()执行完后，会创建一个新线程运行run()。toBlocking().single()是堵塞的，需要等run()运行完才返回结果
 * 4）toObservable()是异步的，是冷响应调用，该方法不会主动创建线程运行run()，只有当调用了toBlocking().single()或subscribe()时，才会去创建线程运行run()
 *
 * 注意：
 * 1）同一个HystrixCommand对象只能执行一次run()
 * 2）observe()中，toBlocking().single()与subscribe()是可以共存的，因为run()是在observe()中被调用的，只调用了一次
 * 3）toObservable()中，toBlocking().single()与subscribe()不可共存，因为run()是在toBlocking().single()或subscribe()中被调用的；
 *    如果同时存在toBlocking().single()和subscribe()，相当于调用了2次run()，会报错
 *
 */
public class HystrixCommandDemo1Test {

    /**
     * execute()
     */
    @Test
    public void testExecute() throws Exception {
        HystrixCommandDemo1 command1 = new HystrixCommandDemo1("World");
        HystrixCommandDemo1 command2 = new HystrixCommandDemo1("Bob");

        String result1 = command1.execute();
        String result2 = command2.execute();

        System.out.println("result: " + result1);
        System.out.println("result: " + result2);
    }

    /**
     * queue()
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testQueue() throws ExecutionException, InterruptedException, TimeoutException {
        HystrixCommandDemo1 command1 = new HystrixCommandDemo1("World");
        HystrixCommandDemo1 command2 = new HystrixCommandDemo1("Bob");

        Future<String> future1 = command1.queue();
        Future<String> future2 = command2.queue();

        System.out.println("------");

        //设置超时等待时间
        //System.out.println("result: " + future1.get(100, TimeUnit.MILLISECONDS));
        System.out.println("result: " + future1.get());
        System.out.println("result: " + future2.get());
    }

    /**
     * observe()
     *
     * @throws Exception
     */
    @Test
    public void testObservable() throws Exception {
        Observable<String> fWorld = new HystrixCommandDemo1("World").observe();
        Observable<String> fBob = new HystrixCommandDemo1("Bob").observe();
        System.out.println("------");
        TimeUnit.MILLISECONDS.sleep(2000);
        /*
        //blocking
        String result1 = fWorld.toBlocking().single();
        String result2 = fBob.toBlocking().single();

        System.out.println("result: " + result1);
        System.out.println("result: " + result2);

        //non-blocking
        fWorld.subscribe(new Observer<String>() {
            Thread t = Thread.currentThread();

            @Override
            public void onError(Throwable e) {
                System.out.println("thread " + t.getName() + ": now execute onError(), error message: " + e.getMessage());
            }

            @Override
            public void onNext(String v) { //v为command的执行结果
                System.out.println("thread " + t.getName() + ": now execute onNext(), result: " + v);
            }

            @Override
            public void onCompleted() {
                System.out.println("thread " + t.getName() + ": now execute onCompleted()");
            }

        });

        //non-blocking
        fBob.subscribe(new Action1<String>() {
            Thread t = Thread.currentThread();
            @Override
            public void call(String v) {
                System.out.println("thread " + t.getName() + ": now execute call(), result: " + v);
            }
        });
        */
    }

    @Test
    public void testToObservable() throws Exception {
        Observable<String> fWorld = new HystrixCommandDemo1("World").toObservable();
        Observable<String> fBob = new HystrixCommandDemo1("Bob").toObservable();

        System.out.println("------");
        TimeUnit.MILLISECONDS.sleep(2000);

        /*//blocking
        String result1 = fWorld.toBlocking().single();
        String result2 = fBob.toBlocking().single();

        System.out.println("result: " + result1);
        System.out.println("result: " + result2);*/

        fWorld.subscribe(new Action1<String>() {
            Thread t = Thread.currentThread();

            @Override
            public void call(String v) {
                System.out.println("thread " + t.getName() + ": now execute call(), result: " + v);
            }
        });

        System.out.println("result: " + fBob.toBlocking().single());

        TimeUnit.MILLISECONDS.sleep(2000);
    }

}
