package com.tomato.hystrix.demo;

import com.netflix.hystrix.HystrixEventType;
import com.tomato.hystrix.demo.hystrix.HystrixObservableCommandDemo1;
import org.junit.Test;
import rx.Observable;
import rx.Observer;

import java.util.concurrent.TimeUnit;

/**
 *
 * HystrixObservableCommand vs HystrixCommand：
 * 1）前者的命令封装在contruct()，后者在run()；前者的fallback处理封装在resumeWithFallback()，后者在getFallBack()
 * 2）前者用主线程执行contruct()，后者另起线程来执行run()
 * 3）前者可以在contruct()中顺序定义多个onNext，当调用subscribe()注册成功后将依次执行这些onNext，后者只能在run()中返回一个值（即一个onNext）
 *
 * HystrixObservableCommand的observe()与toObservable()的区别：
 * 1）observe()会立即执行HelloWorldHystrixObservableCommand.construct()；toObservable()要在toBlocking().single()或subscribe()时才执行HelloWorldHystrixObservableCommand.construct()
 * 2）observe()中，toBlocking().single()和subscribe()可以共存；在toObservable()中不行，因为两者都会触发执行HelloWorldHystrixObservableCommand.construct()，这违反了同一个HelloWorldHystrixObservableCommand对象只能执行construct()一次原则
 * @throws Exception
 */
public class HystrixObservableCommandDemo1Test {

    @Test
    public void testObservable() throws Exception {
        // observe()是异步非堵塞性执行
        Observable<String> hotObservable = new HystrixObservableCommandDemo1("Hlx").observe();
//		TimeUnit.MILLISECONDS.sleep(2000);
//		System.out.println("22222222");
//		String string = hotObservable.toBlocking().single();
//		// single()是堵塞的
//		System.out.println(string);

        // 注册观察者事件
        // subscribe()是非堵塞的
        hotObservable.subscribe(new Observer<String>() {

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

        // 非堵塞
        // - also verbose anonymous inner-class
        // - ignore errors and onCompleted signal
//		hotObservable.subscribe(new Action1<String>() {
//
//			// 相当于上面的onNext()
//			// @Override
//			public void call(String v) {
//				System.out.println("hotObservable call: " + v);
//			}
//
//		});

        TimeUnit.MILLISECONDS.sleep(3000);
    }

    public void testToObservable() throws Exception {
        // toObservable()是异步非堵塞性执行
        Observable<String> coldObservable = new HystrixObservableCommandDemo1("Hlx").toObservable();

        // 注册观察者事件
        // subscribe()是非堵塞的
        // - this is a verbose anonymous inner-class approach and doesn't do assertions
        coldObservable.subscribe(new Observer<String>() {

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

        TimeUnit.MILLISECONDS.sleep(3000);
    }


    @Test
    public void testFailure() throws Exception {
        Observable<String> fWorld = new HystrixObservableCommandDemo1("World", HystrixEventType.TIMEOUT).observe();

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

        TimeUnit.MILLISECONDS.sleep(2000);
    }
}
