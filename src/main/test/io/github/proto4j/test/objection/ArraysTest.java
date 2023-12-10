package io.github.proto4j.test.objection; //@date 27.08.2022

import io.github.proto4j.objection.Marshaller;
import io.github.proto4j.objection.OSharedConfiguration;
import io.github.proto4j.objection.Objection;

import java.awt.*;
import java.io.*;

public class ArraysTest {

    public static void main(String[] args) throws ReflectiveOperationException, IOException {
        Marshaller<Point> marshaller = Objection.createMarshaller();
        ByteArrayOutputStream file = new ByteArrayOutputStream();

        Point a0 = new Point(1, 2);

        // 1. Serialize data
        DataOutput output = new DataOutputStream(file);
        OSharedConfiguration config = marshaller.marshall(a0, output);

        // 2. De-Serialize data
        DataInput input = new DataInputStream(new ByteArrayInputStream(file.toByteArray()));
        Point a1 = marshaller.getInstance(input, config);

        assert a0.equals(a1); // see ArrayModel::equals
    }
}
