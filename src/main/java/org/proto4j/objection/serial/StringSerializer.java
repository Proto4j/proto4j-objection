package org.proto4j.objection.serial; //@date 26.08.2022

import org.proto4j.objection.BasicObjectSerializer;
import org.proto4j.objection.OSerializationContext;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StringSerializer extends BasicObjectSerializer {

    @Override
    public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
        String value = writableObject.toString();
        dataOutput.writeInt(value.length());
        dataOutput.writeBytes(value);
    }

    @Override
    public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
        int len = dataInput.readInt();
        byte[] string =new byte[len];
        for (int i = 0; i < len; i++) {
            string[i] = dataInput.readByte();
        }

        return new String(string, StandardCharsets.UTF_8);
    }

    @Override
    public boolean accept(Class<?> type) {
        return type == String.class;
    }
}
