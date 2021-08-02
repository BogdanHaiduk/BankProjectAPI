package com.test.bankProject.repository;


import com.test.bankProject.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
