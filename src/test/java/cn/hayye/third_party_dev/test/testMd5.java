package cn.hayye.third_party_dev.test;

import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class testMd5 {
    @Test
    public void testUuid(){
        System.out.println(UUID.randomUUID());
        System.out.println(DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes()).toUpperCase());
    }
}
