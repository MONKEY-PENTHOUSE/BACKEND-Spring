package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.Room;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
public class RoomServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomService roomService;

    @Test
    @DisplayName("유저에게 빈 방을 할당")
    public void giveUserRoom() throws Exception {
        User user = userRepository.findById(2L).get();

        Room room = roomService.giveVoidRoomForUser(user);

        System.out.println("user.getRoom() = " + user.getRoom());
        System.out.println("room = " + room);
        Assertions.assertThat(user.getRoom()).isEqualTo(room);
    }

}
