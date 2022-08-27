package org.proto4j.objection; //@date 27.08.2022

import org.proto4j.objection.model.OClass;
import org.proto4j.objection.model.OField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.util.Objects;

public class BasicMarshaller<V> extends AbstractMarshaller<V> {

    public BasicMarshaller() {
    }

    public BasicMarshaller(OSharedConfiguration configuration) {
        super(configuration);
    }

    @Override
    public OSharedConfiguration marshall(V value, DataOutput output) throws IOException, ReflectiveOperationException {
        Objects.requireNonNull(value);
        Objects.requireNonNull(output);

        ObjectSerializer sr = getConfiguration().forType(OClass.class);
        if (sr == null) {
            throw new InvalidClassException("No OClass serializer specified");
        }

        getConfiguration().addType(value.getClass());
        OClass<V> cls = OClass.klass(value, getConfiguration());

        Object o = Objects.requireNonNull(cls.getInstance());
        for (OField field : cls.getDeclaredFields()) {
            Field linked = field.getLinkedField();
            linked.setAccessible(true);
            field.setValue(linked.get(o));
        }

        OSerializationContext ctx = new BasicSerializationContext(cls, null, getConfiguration());
        sr.writeObject(output, cls, ctx);
        return getConfiguration();
    }

    @Override
    public OClass<V> unmarshall(DataInput input, OSharedConfiguration configuration) throws IOException {
        Objects.requireNonNull(input);
        Objects.requireNonNull(configuration);

        setConfiguration(configuration);
        ObjectSerializer sr = getConfiguration().forType(OClass.class);
        if (sr == null) {
            throw new InvalidClassException("No OClass serializer specified");
        }

        OSerializationContext ctx = new BasicSerializationContext(null, null, getConfiguration());
        //noinspection unchecked
        return (OClass<V>) sr.getInstance(OClass.class, input, ctx);
    }
}
