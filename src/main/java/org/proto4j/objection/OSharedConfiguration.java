package org.proto4j.objection; //@date 25.08.2022

import java.nio.charset.StandardCharsets;

public interface OSharedConfiguration {

    ObjectSerializer forType(Class<?> type);

    Class<?> forName(String name);

    default Class<?> forName(byte[] name) throws ClassNotFoundException {
        return forName(new String(name, StandardCharsets.UTF_8));
    }

    boolean isRegistered(String name);

    default boolean isRegistered(byte[] name) {
        return isRegistered(new String(name, StandardCharsets.UTF_8));
    }

    void addType(Class<?> cls);

    void addSerializer(ObjectSerializer serializer);
}
