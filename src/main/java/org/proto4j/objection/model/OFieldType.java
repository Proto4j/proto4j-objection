package org.proto4j.objection.model;//@date 25.08.2022

import java.lang.reflect.Field;

public enum OFieldType {
    DEFAULT;

    public static OFieldType valueOf(byte type) {
        return DEFAULT;
    }

    public static byte wrap(Field linkedField) {
        return 0;
    }
}
