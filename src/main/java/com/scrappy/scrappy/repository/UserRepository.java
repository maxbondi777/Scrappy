package com.scrappy.scrappy.repository;
import com.scrappy.scrappy.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}