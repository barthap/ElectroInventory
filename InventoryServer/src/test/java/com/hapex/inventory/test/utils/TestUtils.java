package com.hapex.inventory.test.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

public class TestUtils {
    public static long randId() {
        return new Random().nextLong();
    }

    public static String asJsonString(Object o) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
