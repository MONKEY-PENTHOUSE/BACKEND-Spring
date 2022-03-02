package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dao.LifeStyle;
import com.monkeypenthouse.core.dao.LoginType;
import com.monkeypenthouse.core.dao.Room;
import com.monkeypenthouse.core.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select m from User m where m.loginType = :loginType and m.email = :email")
    Optional<User> findByEmailAndLoginType(@Param("email") String email, @Param("loginType") LoginType loginType);
    Optional<User> findByEmail(String email);
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.room = :room WHERE u.id = :userId")
    void updateRoomId(@Param("userId") Long userId, @Param("room") Room room);

    boolean existsByEmail(String email);

    boolean existsByPhoneNum(String phoneNum);

    Optional<User> findByPhoneNum(String phoneNum);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    int updatePassword(@Param("password") String password,
                      @Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.lifeStyle = :lifeStyle WHERE u.id = :id")
    int updateLifeStyle(@Param("lifeStyle") LifeStyle lifeStyle, Long id);
}
