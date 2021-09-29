package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.Room;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.repository.RoomRepository;
import com.monkeypenthouse.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Room giveVoidRoomForUser(User user) {
        roomRepository.updateUserIdForVoidRoom(user.getId(), user.getUserRole());
        Optional<Room> roomOptional = roomRepository.findByUserId(user.getId());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            userRepository.updateRoomId(user.getId(), room);
            return room;
        } else {
            return null;
        }

    }
}
