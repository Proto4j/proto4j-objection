package io.github.proto4j.test.objection; //@date 28.08.2022

import io.github.proto4j.objection.Marshaller;
import io.github.proto4j.objection.OSharedConfiguration;
import io.github.proto4j.objection.Objection;
import io.github.proto4j.test.objection.model.Lists;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class ListsTest {

    public static void main(String[] args) throws ReflectiveOperationException, IOException {
        Marshaller<Lists> marshaller = Objection.createMarshaller();
        ByteArrayOutputStream file = new ByteArrayOutputStream();

        Lists a0 = new Lists(List.of("Hello", "World"), new LinkedList<>());

        // 1. Serialize data
        DataOutput output = new DataOutputStream(file);
        OSharedConfiguration config = marshaller.marshall(a0, output);

        // 2. De-Serialize data
        DataInput input = new DataInputStream(new ByteArrayInputStream(file.toByteArray()));
        Lists a1 = marshaller.getInstance(input, config);

        assert a0.equals(a1) : "Not equal"; // see ArrayModel::equals
    }

}
