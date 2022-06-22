package com.triple.mileage.user.repository;

import com.triple.mileage.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<UUID, User> {


}
