package org.proto4j.objection.serial; //@date 26.08.2022

import org.proto4j.objection.BasicObjectSerializer;
import org.proto4j.objection.OSerializationContext;
import org.proto4j.objection.ObjectSerializer;
import org.proto4j.objection.Objection;
import org.proto4j.objection.model.OClass;
import org.proto4j.objection.model.OField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidClassException;

public class OClassSerializer extends BasicObjectSerializer {

    @Override
    public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
        OClass<?> classInfo = (OClass<?>) writableObject;
        byte[]    name      = classInfo.getBufferedName();
        dataOutput.writeByte(name.length);
        dataOutput.write(name);
        dataOutput.writeInt(classInfo.getModifiers());
        dataOutput.writeInt(classInfo.getClassId());

        ObjectSerializer fsr = ctx.getConfig().forType(OField.class);
        if (fsr == null) {
            throw new NullPointerException("Could not serialize OField.class");
        }
        OField[] fields = classInfo.getDeclaredFields();
        dataOutput.writeInt(fields.length);

        for (OField field : fields) {
            fsr.writeObject(dataOutput, field, ctx);
        }
    }

    @Override
    public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
        byte   nameLength = dataInput.readByte();
        byte[] name       = new byte[nameLength];
        for (int i = 0; i < nameLength; i++) {
            name[i] = dataInput.readByte();
        }


        Class<?> linkedType;
        try {
            if (ctx.getConfig().isRegistered(name)) {
                linkedType = ctx.getConfig().forName(name);
            } else throw new InvalidClassException("Unsafe Operation: Class not defined!");

        } catch (ClassNotFoundException e) {
            throw new InvalidClassException(e.getMessage());

        } finally {
            for (int i = 0; i < nameLength; i++) {
                name[i] = 0;
            }
        }
        int       mod    = dataInput.readInt();
        int       id     = dataInput.readInt();
        OClass<?> oClass = OClass.klass(linkedType, ctx.getConfig());
        if (oClass.getModifiers() != mod || oClass.getClassId() != id) {
            throw new InvalidClassException("Invalid loaded class: Checksum mismatch");
        }

        OSerializationContext classCtx   = Objection.createContext(oClass, null, ctx.getConfig());
        ObjectSerializer      serializer = ctx.getConfig().forType(OField.class);

        int field_count = dataInput.readInt();
        for (int i = 0; i < field_count; i++) {
            serializer.getInstance(OField.class, dataInput, classCtx);
        }
        return oClass;
    }

    @Override
    public boolean accept(Class<?> type) {
        return type == OClass.class;
    }
}
