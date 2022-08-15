package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.repository.entity.UserJoinLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJoinLogRepository extends JpaRepository<UserJoinLog, Long> {
}
