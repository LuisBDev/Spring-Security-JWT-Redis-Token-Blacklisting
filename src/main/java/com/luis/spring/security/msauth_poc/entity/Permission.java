package com.luis.spring.security.msauth_poc.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Luis Balarezo
 **/
@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

}
