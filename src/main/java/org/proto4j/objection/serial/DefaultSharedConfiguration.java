package org.proto4j.objection.serial; //@date 26.08.2022

import org.proto4j.objection.BasicSharedConfiguration;
import org.proto4j.objection.ObjectSerializer;

import java.util.Objects;

public class DefaultSharedConfiguration extends BasicSharedConfiguration {

    public DefaultSharedConfiguration() {
        getSerializers().add(new NumberSerializer.ByteSerializer());
        getSerializers().add(new NumberSerializer.CharacterSerializer());
        getSerializers().add(new NumberSerializer.DoubleSerializer());
        getSerializers().add(new NumberSerializer.FloatSerializer());
        getSerializers().add(new NumberSerializer.LongSerializer());
        getSerializers().add(new NumberSerializer.ShortSerializer());
        getSerializers().add(new NumberSerializer.IntegerSerializer());
        getSerializers().add(new OClassSerializer());
        getSerializers().add(new OFieldSerializer());
        getSerializers().add(new StringSerializer());
        getSerializers().add(new SequenceSerializer.CollectionSerializer());
        getSerializers().add(new SequenceSerializer.KeyValueSerializer());
    }

    @Override
    public ObjectSerializer forType(Class<?> type) {
        ObjectSerializer sr =  super.forType(type);
        if (sr == null && type.isArray()) {
            if (!type.getComponentType().isArray()) {
                return SequenceSerializer.ArraySerializer.createArraySerializer(type.getComponentType());
            } else throw new UnsupportedOperationException("MultiArrays not implemented");
        }
        return sr;
    }

    public synchronized void addSerializer(ObjectSerializer serializer) {
        Objects.requireNonNull(serializer);
        getSerializers().add(serializer);
    }

    public synchronized void addType(Class<?> type) {
        Objects.requireNonNull(type);
        getRegisteredClasses().put(type.getName(), type);
    }

}
