package com.tomato.hystrix.demo.service;

import com.alibaba.fastjson.JSON;
import com.tomato.hystrix.demo.config.ErrorType;
import com.tomato.hystrix.demo.entity.Message;
import com.tomato.hystrix.demo.entity.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public String register(User user, ErrorType type) {
        System.out.println("验证手机号是否存在，手机号:" + user.getPhoneNumber());

        System.out.println("生成uid：" + user.getUid());

        System.out.println("保存用户注册信息");

        if (type.equals(ErrorType.TIMEOUT)) {
            try {
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if (type.equals(ErrorType.RUNTIME_EXCEPTION)) {
            throw new RuntimeException(user.getPhoneNumber() + " register run exception");
        }
        System.out.println("获取用户权益");

        Message message = new Message();
        message.setCode(200);
        message.setMsg("注册成功");
        return JSON.toJSONString(message);
    }
}
