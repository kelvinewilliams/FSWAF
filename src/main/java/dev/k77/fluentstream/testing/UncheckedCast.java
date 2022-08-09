package dev.k77.fluentstream.testing;

import java.util.HashMap;
import java.util.Map;

public class UncheckedCast {
    public static Map getRawMap() {
        Map rawMap = new HashMap();
        rawMap.put("String1", "String 2");
        rawMap.put("String2", "String A");
        rawMap.put("String3", "String b");
        return rawMap;
    }
}
