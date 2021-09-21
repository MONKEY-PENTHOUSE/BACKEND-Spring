package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select m from User m where m.loginType = :loginType and m.email = :email")
    Optional<User> findByEmail(@Param("email") String email, @Param("loginType") LoginType loginType);
}
