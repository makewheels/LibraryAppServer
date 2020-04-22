package com.eg.libraryappserver.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.util.UUID;

/**
 * @time 2020-04-22 00:41
 */
public class RandomUtil {
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getRandomString() {
        String randomNanoId = NanoIdUtils.randomNanoId();
        while (randomNanoId.contains("-") || randomNanoId.contains("_")) {
            randomNanoId = NanoIdUtils.randomNanoId();
        }
        return randomNanoId;
    }
}
