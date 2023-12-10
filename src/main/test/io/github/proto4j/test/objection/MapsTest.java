package io.github.proto4j.test.objection; //@date 28.08.2022

import io.github.proto4j.objection.Marshaller;
import io.github.proto4j.objection.OSharedConfiguration;
import io.github.proto4j.objection.Objection;
import io.github.proto4j.test.objection.model.Maps;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MapsTest {

    public static void main(String[] args) throws ReflectiveOperationException, IOException {
        Marshaller<Maps> marshaller = Objection.createMarshaller();
        ByteArrayOutputStream file = new ByteArrayOutputStream();

        Map<String, Long> map = new HashMap<>();
        map.put("Hello", 10L);
        map.put("World", -11L);
        Maps a0 = new Maps(map);

        // 1. Serialize data
        DataOutput output = new DataOutputStream(file);
        OSharedConfiguration config = marshaller.marshall(a0, output);

        // 2. De-Serialize data
        DataInput input = new DataInputStream(new ByteArrayInputStream(file.toByteArray()));
        Maps a1 = marshaller.getInstance(input, config);

        assert a0.equals(a1) : "Not equal"; // see ArrayModel::equals
    }

}
