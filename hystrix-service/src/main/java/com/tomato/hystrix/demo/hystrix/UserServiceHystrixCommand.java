package com.tomato.hystrix.demo.hystrix;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.tomato.hystrix.demo.config.ErrorType;
import com.tomato.hystrix.demo.entity.Message;
import com.tomato.hystrix.demo.entity.User;
import com.tomato.hystrix.demo.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserServiceHystrixCommand extends HystrixCommand<String> {

    private User user;

    private ErrorType type;

    private UserService userService;


    public UserServiceHystrixCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
    }


    @Override
    protected String run() {
        System.out.println(user.getPhoneNumber() + " execute UserServiceHystrixCommand.run()");
        return userService.register(user, this.type);
    }

    @Override
    protected String getFallback() {
        System.out.println(user.getPhoneNumber() + " register failed, execute fallback.");
        Message message = new Message();
        message.setCode(-100);
        message.setMsg("服务器异常，降级处理。");
        return JSON.toJSONString(message);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setType(ErrorType type) {
        this.type = type;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
