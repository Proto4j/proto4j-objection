package org.proto4j.test.objection; //@date 27.08.2022

import org.proto4j.objection.Marshaller;
import org.proto4j.objection.OSharedConfiguration;
import org.proto4j.objection.Objection;
import org.proto4j.test.objection.model.ArrayModel;

import java.io.*;

public class ArraysTest {

    public static void main(String[] args) throws ReflectiveOperationException, IOException {
        Marshaller<ArrayModel> marshaller = Objection.createMarshaller();
        ByteArrayOutputStream  file       = new ByteArrayOutputStream();

        ArrayModel a0 = new ArrayModel(new int[]{1, 2, 3}, new String[]{"4", "five"});

        // 1. Serialize data
        DataOutput           output = new DataOutputStream(file);
        OSharedConfiguration config = marshaller.marshall(a0, output);

        // 2. De-Serialize data
        DataInput  input = new DataInputStream(new ByteArrayInputStream(file.toByteArray()));
        ArrayModel a1    = marshaller.getInstance(input, config);

        assert a0.equals(a1); // see ArrayModel::equals
    }
}
