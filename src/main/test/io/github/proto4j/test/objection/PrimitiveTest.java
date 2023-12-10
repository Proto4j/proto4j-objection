package io.github.proto4j.test.objection; //@date 27.08.2022


import io.github.proto4j.objection.Marshaller;
import io.github.proto4j.objection.OSharedConfiguration;
import io.github.proto4j.objection.Objection;
import io.github.proto4j.test.objection.model.Primitives;

import java.io.*;

//! Model class: .model.Primitives
public class PrimitiveTest {

    public static void main(String[] args) throws ReflectiveOperationException, IOException {
        Marshaller<Primitives> marshaller = Objection.createMarshaller();
        ByteArrayOutputStream file = new ByteArrayOutputStream();

        // Test values beginning by 1:
        Primitives p0 = new Primitives(1, 2.2f, 3.3, (char) 4, (byte) 5, (short) 6, 7);

        // 1. Serialize data
        DataOutput output = new DataOutputStream(file);
        OSharedConfiguration config = marshaller.marshall(p0, output);

        // 2. De-Serialize data
        DataInput input = new DataInputStream(new ByteArrayInputStream(file.toByteArray()));
        Primitives p1 = marshaller.getInstance(input, config);

        assert p0.equals(p1);
    }
}
