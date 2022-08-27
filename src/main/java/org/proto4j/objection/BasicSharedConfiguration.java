package org.proto4j.objection; //@date 26.08.2022

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BasicSharedConfiguration implements OSharedConfiguration {

    private final ConcurrentMap<String, Class<?>> registeredClasses = new ConcurrentHashMap<>();
    private final List<ObjectSerializer>          serializers       = new LinkedList<>();

    @Override
    public Class<?> forName(String name) {
        Objects.requireNonNull(name);

        if (!isRegistered(name)) {
            return null;
        }
        return registeredClasses.get(name);
    }

    @Override
    public ObjectSerializer forType(Class<?> type) {
        Objects.requireNonNull(type);

        for (ObjectSerializer serializer : serializers) {
            if (serializer.accept(type)) {
                return serializer;
            }
        }
        return null;
    }

    @Override
    public boolean isRegistered(String name) {
        return registeredClasses.containsKey(name);
    }

    public ConcurrentMap<String, Class<?>> getRegisteredClasses() {
        return registeredClasses;
    }

    public List<ObjectSerializer> getSerializers() {
        return serializers;
    }
}
