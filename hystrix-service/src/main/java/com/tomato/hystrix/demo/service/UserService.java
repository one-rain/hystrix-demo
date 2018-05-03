package com.tomato.hystrix.demo.service;

import com.tomato.hystrix.demo.config.ErrorType;
import com.tomato.hystrix.demo.entity.User;

public interface UserService {

    public String register(User user, ErrorType type);
}
