package com.luis.spring.security.msauth_poc.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * @author Luis Balarezo
 **/

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Set<String> roles;
    private Set<String> permissions;
    private Instant createdAt;
}
