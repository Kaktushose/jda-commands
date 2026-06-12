package io.github.kaktushose.jdac.definitions.interactions.internal;

import java.nio.ByteBuffer;
import java.util.Base64;

public class Base64Utils {
    public static long decodeLong(String raw) {
        byte[] bytes = Base64.getUrlDecoder().decode(raw);
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static String encodeLong(long raw) {
        byte[] data = ByteBuffer.allocate(8)
                .putLong(raw)
                .array();

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(data);
    }

    public static String encodeInt(int raw) {
        byte[] data = ByteBuffer.allocate(4)
                .putInt(raw)
                .array();

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(data);
    }
}
