package com.luis.spring.security.msauth_poc.mapper;

import com.luis.spring.security.msauth_poc.dto.response.UserResponse;
import com.luis.spring.security.msauth_poc.entity.Permission;
import com.luis.spring.security.msauth_poc.entity.Role;
import com.luis.spring.security.msauth_poc.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Luis Balarezo
 **/
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToNames")
    @Mapping(target = "permissions", source = "roles", qualifiedByName = "rolesToPermissions")
    UserResponse toUserResponse(User user);

    @Named("rolesToNames")
    default Set<String> rolesToNames(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Named("rolesToPermissions")
    default Set<String> rolesToPermissions(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}
