package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository가 없어도 JpaRepository에 의해 자동으로 빈으로 등록이 된다.
public interface UserRepository extends JpaRepository<User, Integer> {
}
