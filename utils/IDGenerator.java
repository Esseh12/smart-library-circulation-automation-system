package utils;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static AtomicInteger itemCounter = new AtomicInteger(1000);
    private static AtomicInteger userCounter = new AtomicInteger(100);

    public static String generateItemId(String type) {
        String prefix = type.substring(0, 1).toUpperCase();
        return prefix + itemCounter.getAndIncrement();
    }

    public static String generateUserId() {
        return "U" + userCounter.getAndIncrement();
    }

    public static void setItemCounter(int value) {
        itemCounter.set(value);
    }

    public static void setUserCounter(int value) {
        userCounter.set(value);
    }
}
