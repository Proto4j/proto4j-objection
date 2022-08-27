package org.proto4j.objection;//@date 27.08.2022

import org.proto4j.objection.model.OClass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Marshaller<V> {

    OSharedConfiguration marshall(V value, DataOutput output) throws IOException, ReflectiveOperationException;

    OClass<V> unmarshall(DataInput input, OSharedConfiguration configuration) throws IOException;

    default V getInstance(DataInput input, OSharedConfiguration configuration) throws IOException,
            ReflectiveOperationException {
        OClass<V> cls = unmarshall(input, configuration);
        return cls.newInstance();
    }

}
