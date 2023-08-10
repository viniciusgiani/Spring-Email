package com.spring.email.service;

import com.spring.email.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User saveUser(User user);

    Boolean verifyToken(String token);

}
