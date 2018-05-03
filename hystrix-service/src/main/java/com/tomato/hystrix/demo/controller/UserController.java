package com.tomato.hystrix.demo.controller;

import com.tomato.hystrix.demo.config.ErrorType;
import com.tomato.hystrix.demo.entity.User;
import com.tomato.hystrix.demo.hystrix.UserServiceHystrixCommand;
import com.tomato.hystrix.demo.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    @ResponseBody
    public String register() {
        User user = new User();
        user.setPhoneNumber("13512345678");
        user.setUid("test_12345678");
        System.out.println("user " + user.getPhoneNumber() + " start register.");

        String type = env.getProperty("customize.hystrix.test.exception");
        if (StringUtils.isEmpty(type)) {
            type = "success";
        }
        //return userService.register(user);
        UserServiceHystrixCommand ushc = new UserServiceHystrixCommand();
        ushc.setUserService(userService);
        ushc.setUser(user);
        ushc.setType(ErrorType.valueOf(type));

        return ushc.execute();
    }

}
