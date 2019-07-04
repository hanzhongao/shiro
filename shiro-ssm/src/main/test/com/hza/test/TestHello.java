package com.hza.test;

import com.hza.ssm.util.EncryptSalt;
import com.hza.ssm.util.EncryptUtil;

public class TestHello {
    public static void main(String[] args) {
        System.out.println(EncryptUtil.encode("hello"));
    }
}
