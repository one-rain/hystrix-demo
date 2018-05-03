package com.tomato.hystrix.demo.hystrix;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * command合并：HystrixCollapser
 */
public class HystrixCommandDemo6 extends HystrixCollapser<List<String>, String, Integer> {

    private final Integer key;

    public HystrixCommandDemo6(Integer key) {
        this.key = key;
    }

    @Override
    public Integer getRequestArgument() {
        return key;
    }

    @Override
    protected HystrixCommand<List<String>> createCommand(final Collection<CollapsedRequest<String, Integer>> requests) {
        return new BatchCommand(requests);
    }

    @Override
    protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Integer>> requests) {
        int count = 0;
        for (CollapsedRequest<String, Integer> request : requests) {
            request.setResponse(batchResponse.get(count++));
        }
    }

    private static final class BatchCommand extends HystrixCommand<List<String>> {

        private final Collection<HystrixCollapser.CollapsedRequest<String, Integer>> requests;

        private BatchCommand(Collection<HystrixCollapser.CollapsedRequest<String, Integer>> requests) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("testGroup"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("testKey")));
            this.requests = requests;
        }

        @Override
        protected List<String> run() {
            List<String> response = new ArrayList<String>();
            for (HystrixCollapser.CollapsedRequest<String, Integer> request : requests) {
                response.add("testKey: " + request.getArgument());
            }
            return response;
        }

    }
}
