package com.monkeypenthouse.core.dummy;

import com.monkeypenthouse.core.dao.SmsAuthNum;
import com.monkeypenthouse.core.repository.SmsAuthNumRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmsAuthNumTest {

//    @Autowired
//    private SmsAuthNumRepository smsAuthNumRepository;
//
//    // Redis에 authnum 넣어보기
//    @Test
//    void addSmsAuthNum() {
//        SmsAuthNum authNum = new SmsAuthNum("01022583520", "456789");
//
//        smsAuthNumRepository.save(authNum);
//
//        SmsAuthNum savedNum = smsAuthNumRepository.findById(authNum.getUserPhoneNum()).get();
//
//        System.out.println("num = " + savedNum);
//
//        System.out.println(smsAuthNumRepository.count());
//
//    }
}
