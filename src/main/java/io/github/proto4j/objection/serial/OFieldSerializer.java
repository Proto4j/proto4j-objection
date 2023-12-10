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

package io.github.proto4j.objection.serial; //@date 25.08.2022

import io.github.proto4j.objection.BasicObjectSerializer;
import io.github.proto4j.objection.OSerializationContext;
import io.github.proto4j.objection.ObjectSerializer;
import io.github.proto4j.objection.model.OField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.charset.StandardCharsets;

/**
 * A small wrapper for reading and writing {@link OField} objects into a binary
 * format.
 *
 * @author MatrixEditor
 * @version 0.2.0
 * @see OField
 */
public class OFieldSerializer extends BasicObjectSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Class<?> type) {
        return type == OField.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
        OField reference = (OField) writableObject;
        dataOutput.writeByte(reference.getFieldType());
        dataOutput.writeByte(reference.getVersion());

        String name = reference.getName();
        dataOutput.writeByte((byte) name.length());
        dataOutput.writeBytes(name);

        Object value = reference.getValue();
        ObjectSerializer sr = ctx.getConfig().forType(value.getClass());
        if (sr != null) {
            sr.writeObject(dataOutput, value, ctx);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
        // reference is null
        byte ftype = dataInput.readByte();
        byte version = dataInput.readByte();

        byte name_len = dataInput.readByte();
        byte[] name = new byte[name_len];

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

        ObjectSerializer sr = ctx.getConfig().forType(field.getLinkedFieldType());
        if (sr != null) {
            field.setValue(sr.getInstance(field.getLinkedFieldType(), dataInput, ctx));
        }
        return field;
    }

}
