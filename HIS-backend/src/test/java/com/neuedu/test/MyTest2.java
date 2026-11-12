package com.neuedu.test;

import com.neuedu.entity.Student;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MyTest2 {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);
    }

    public static int fun(int a) {
        try {
            int i = 10;
            return i / a;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}
