package com.monkeypenthouse.core.dummy;

import com.monkeypenthouse.core.dao.Room;
import com.monkeypenthouse.core.dao.Authority;
import com.monkeypenthouse.core.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootTest
public class RoomTest {
    @Autowired
    private RoomRepository repository;

    @Test
    @DisplayName("room 더미데이터 넣는 테스트")
    public void insertDummies() {
        List<Room> dummies = new ArrayList<>();

        String[] arr = new String[]{"A", "B", "C", "D", "F", "E", "G"};
        Stream<String> stream = Arrays.stream(arr);
        stream.forEach(str -> {
            IntStream.rangeClosed(1, 999).forEach(i -> {

                String id;
                if (i < 10) {
                    id = str + "000" + i;
                } else if (i < 100) {
                    id = str +  "00" + i;
                } else {
                    id = str + "0" + i;
                }

                Authority authority;
                if (str.equals("A") && i <= 100) {
                    authority = Authority.ADMIN;
                } else {
                    authority = Authority.USER;
                }

                if (!(id.endsWith("0444") || id.endsWith("4444") || id.endsWith("1818"))) {
                    System.out.println("id = " + id);
                    Room room = Room.builder()
                            .id(id).authority(authority).build();

                    dummies.add(room);
                }
            });
        });

        repository.saveAll(dummies);
    }
}
