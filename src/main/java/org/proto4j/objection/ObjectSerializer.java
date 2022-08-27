package org.proto4j.objection; //@date 25.08.2022

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Objects;

public interface ObjectSerializer {

    void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException;

    Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException;

    default <T> T getTypeInstance(Class<T> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
        if (!accept(Objects.requireNonNull(type))) {
            throw new InvalidClassException(type.getSimpleName() + " is not supported");
        }
        return type.cast(getInstance(type, dataInput, ctx));
    }

    boolean accept(Class<?> type);

}
