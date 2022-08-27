package org.proto4j.objection; //@date 26.08.2022

import org.proto4j.objection.model.OClass;
import org.proto4j.objection.model.OField;
import org.proto4j.objection.serial.DefaultSharedConfiguration;

import java.io.*;
import java.util.Objects;
import java.util.function.Supplier;

public final class Objection {

    private Objection() {}

    public static <T> Marshaller<T> createMarshaller() {
        return createMarshaller(getDefaultConfiguration());
    }

    public static <T> Marshaller<T> createMarshaller(OSharedConfiguration config) {
        return new BasicMarshaller<>(config);
    }

    public static DataInput createDataInput(Supplier<? extends InputStream> supplier) {
        return createDataInput(supplier.get());
    }

    public static DataInput createDataInput(InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        return new DataInputStream(inputStream);
    }

    public static DataOutput createDataOutput(Supplier<? extends OutputStream> supplier) {
        return createDataOutput(supplier.get());
    }

    public static DataOutput createDataOutput(OutputStream outputStream) {
        Objects.requireNonNull(outputStream);
        return new DataOutputStream(outputStream);
    }

    public static OSerializationContext createContext(OClass<?> classInfo, OField ref, OSharedConfiguration config) {
        Objects.requireNonNull(config);
        return new BasicSerializationContext(classInfo, ref, config);
    }

    public static OSharedConfiguration getDefaultConfiguration() {
        return new DefaultSharedConfiguration();
    }


}
