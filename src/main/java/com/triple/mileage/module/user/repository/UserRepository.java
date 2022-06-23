package com.triple.mileage.module.user.repository;

import com.triple.mileage.module.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, UUID> {

}
