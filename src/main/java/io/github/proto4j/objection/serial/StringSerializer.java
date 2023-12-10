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
import io.github.proto4j.objection.OSerializationContext;
import io.github.proto4j.objection.serial.SequenceSerializer.ArraySerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Alternative version of the {@link ArraySerializer} for componentType {@link Byte}.
 */
public class StringSerializer extends BasicObjectSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
        String value = writableObject.toString();
        dataOutput.writeInt(value.length());
        dataOutput.writeBytes(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
        int len = dataInput.readInt();
        byte[] string = new byte[len];
        for (int i = 0; i < len; i++) {
            string[i] = dataInput.readByte();
        }

        return new String(string, StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Class<?> type) {
        return type == String.class;
    }
}
