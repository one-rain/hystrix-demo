package com.tomato.hystrix.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * command请求缓存
 */
public class HystrixCommandDemo7 extends HystrixCommand<Boolean> {

    private final int value;
    private final String value1;

    public HystrixCommandDemo7(int value, String value1) {
        super(HystrixCommandGroupKey.Factory.asKey("testCacheGroup"));
        this.value = value;
        this.value1 = value1;
    }

    @Override
    protected Boolean run() {
        System.out.println(value + " execute run()...");
        return value == 0 || value % 2 == 0;
    }

    @Override
    protected String getCacheKey() {
        //System.out.println(value + " use cache.");
        return String.valueOf(value) + value1;
    }
}
