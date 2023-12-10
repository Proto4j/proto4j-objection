/*
 * MIT License
 *
 * Copyright (c) 2022 Proto4j
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.proto4j.objection.serial; //@date 26.08.2022

import io.github.proto4j.objection.BasicObjectSerializer;
import io.github.proto4j.objection.BasicSerializationContext;
import io.github.proto4j.objection.OSerializationContext;
import io.github.proto4j.objection.ObjectSerializer;
import io.github.proto4j.objection.model.OClass;
import io.github.proto4j.objection.model.OField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidClassException;

/**
 * A small wrapper for reading and writing {@link OClass} objects into a binary
 * format.
 *
 * @author MatrixEditor
 * @version 0.2.0
 * @see OClass
 */
public class OClassSerializer extends BasicObjectSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
        OClass<?> classInfo = (OClass<?>) writableObject;
        byte[] name = classInfo.getBufferedName();
        dataOutput.writeByte(classInfo.getVersion());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
        byte version = dataInput.readByte();
        byte nameLength = dataInput.readByte();
        byte[] name = new byte[nameLength];
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
        int mod = dataInput.readInt();
        int id = dataInput.readInt();
        OClass<?> oClass = OClass.klass(linkedType, ctx.getConfig());
        if (oClass.getModifiers() != mod || oClass.getClassId() != id) {
            throw new InvalidClassException("Invalid loaded class: Checksum mismatch");
        }

        OSerializationContext classCtx = new BasicSerializationContext(oClass, null, ctx.getConfig());
        ObjectSerializer serializer = ctx.getConfig().forType(OField.class);

        int field_count = dataInput.readInt();
        for (int i = 0; i < field_count; i++) {
            serializer.getInstance(OField.class, dataInput, classCtx);
        }
        return oClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Class<?> type) {
        return type == OClass.class;
    }
}
