package com.luis.spring.security.msauth_poc.service;


import com.luis.spring.security.msauth_poc.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID id);

    void deleteUser(UUID id);

}
