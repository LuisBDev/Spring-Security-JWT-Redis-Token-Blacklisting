package com.luis.spring.security.msauth_poc.repository;

import com.luis.spring.security.msauth_poc.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Luis Balarezo
 **/
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {


}
