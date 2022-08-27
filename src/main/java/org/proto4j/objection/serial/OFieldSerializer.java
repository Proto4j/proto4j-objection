package org.proto4j.objection.serial; //@date 25.08.2022

import org.proto4j.objection.BasicObjectSerializer;
import org.proto4j.objection.OSerializationContext;
import org.proto4j.objection.ObjectSerializer;
import org.proto4j.objection.model.OField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.charset.StandardCharsets;

public class OFieldSerializer extends BasicObjectSerializer {

    @Override
    public boolean accept(Class<?> type) {
        return type == OField.class;
    }

    @Override
    public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
        OField reference = (OField) writableObject;
        dataOutput.writeByte(reference.getFieldType());
        dataOutput.writeByte(reference.getVersion());

        String name = reference.getName();
        dataOutput.writeByte((byte) name.length());
        dataOutput.writeBytes(name);

        Object           value = reference.getValue();
        ObjectSerializer sr    = ctx.getConfig().forType(value.getClass());
        if (sr != null) {
            sr.writeObject(dataOutput, value, ctx);
        }
    }

    @Override
    public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
        // reference is null
        byte ftype = dataInput.readByte();
        byte version = dataInput.readByte();

        byte   name_len = dataInput.readByte();
        byte[] name     = new byte[name_len];

        for (int i = 0; i < name_len; i++) {
            name[i] = dataInput.readByte();
        }

        OField field;
        try {
            field = ctx.getClassInfo().getDeclaredField(new String(name, StandardCharsets.UTF_8));
            if (field == null) {
                throw new NullPointerException("Field not declared");
            }
            if (version != field.getVersion() || ftype != field.getFieldType()) {
                throw new InvalidObjectException("Field version of type does not match");
            }
        } finally {
            for (int i = 0; i < name_len; i++) {
                name[i] = 0;
            }
        }

        ObjectSerializer sr = ctx.getClassInfo().getConfiguration().forType(field.getLinkedFieldType());
        if (sr != null) {
            field.setValue(sr.getInstance(field.getLinkedFieldType(), dataInput, ctx));
        }
        return field;
    }

}
