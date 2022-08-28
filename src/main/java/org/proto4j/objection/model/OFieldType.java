package org.proto4j.objection.model;//@date 25.08.2022

import java.lang.reflect.Field;

//! INFO: This type can be modified and should be used for internal checks. Yet,
//! it is not used in any context.
public enum OFieldType {
    DEFAULT;

    public static OFieldType valueOf(byte type) {
        // not implemented yet
        return DEFAULT;
    }

    public static byte wrap(Field linkedField) {
        return 0;
    }
}
