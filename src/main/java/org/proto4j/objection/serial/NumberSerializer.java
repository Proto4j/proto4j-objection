package org.proto4j.objection.serial; //@date 25.08.2022

import org.proto4j.objection.BasicObjectSerializer;
import org.proto4j.objection.OSerializationContext;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NumberSerializer {

    public static class LongSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return type == Long.class || type == Long.TYPE;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
                throws IOException {
            dataOutput.writeLong((long)writableObject);
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
            dataOutput.writeInt((int)writableObject);
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
            dataOutput.writeShort((short)writableObject);
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
            dataOutput.writeByte((byte)writableObject);
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
            dataOutput.writeChar((char)writableObject);
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
            dataOutput.writeDouble((double)writableObject);
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
            dataOutput.writeFloat((float)writableObject);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            return dataInput.readFloat();
        }
    }
}
