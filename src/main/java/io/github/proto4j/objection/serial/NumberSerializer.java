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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A utility class containing all primitive serializers. They use the in-built
 * methods by the {@link DataInput} and {@link DataOutput} objects.
 *
 * @author MatrixEditor
 * @version 0.2.0
 */
public class NumberSerializer {

    public static class LongSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return type == Long.class || type == Long.TYPE;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
                throws IOException {
            dataOutput.writeLong((long) writableObject);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            return dataInput.readLong();
        }
    }

    public static class IntegerSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return type == Integer.class || type == Integer.TYPE;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
                throws IOException {
            dataOutput.writeInt((int) writableObject);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            return dataInput.readInt();
        }
    }

    public static class ShortSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return type == Short.class || type == Short.TYPE;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
                throws IOException {
            dataOutput.writeShort((short) writableObject);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            return dataInput.readShort();
        }
    }

    public static class ByteSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return type == Byte.class || type == Byte.TYPE;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
                throws IOException {
            dataOutput.writeByte((byte) writableObject);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            return dataInput.readByte();
        }
    }

    public static class CharacterSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return type == Character.class || type == Character.TYPE;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
                throws IOException {
            dataOutput.writeChar((char) writableObject);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            return dataInput.readChar();
        }
    }

    public static class DoubleSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return type == Double.class || type == Double.TYPE;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
                throws IOException {
            dataOutput.writeDouble((double) writableObject);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            return dataInput.readDouble();
        }
    }

    public static class FloatSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return type == Float.class || type == Float.TYPE;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
                throws IOException {
            dataOutput.writeFloat((float) writableObject);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            return dataInput.readFloat();
        }
    }
}
