package com.example.demo.data.access.interfaces;

import com.example.demo.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserDao extends JpaRepository<UserEntity, Long> {

}
