package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.Room;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.dao.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select m from User m where m.loginType = :loginType and m.email = :email")
    Optional<User> findByEmailAndLoginType(@Param("email") String email, @Param("loginType") LoginType loginType);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.room = :room WHERE u.id = :userId")
    void updateRoomId(@Param("userId") Long userId, @Param("room") Room room);
}
