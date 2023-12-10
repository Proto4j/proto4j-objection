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
import io.github.proto4j.objection.ObjectSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A utility class containing all sequence serializers. The types that can be
 * serialized are: Arrays (one dimension), {@link Map}s and {@link Collection}s
 *
 * @author MatrixEditor
 * @version 0.2.0
 */
public class SequenceSerializer {

    /**
     * <pre>
     * ┌────────────────────────────────────────────────────────┐
     * │ Array                                                  │
     * ├──────────────────┬───────────────┬─────────────────────┤
     * │ dimensions: byte │ dim0_len: int │ dim0_values: byte[] │
     * └──────────────────┴───────────────┴─────────────────────┘
     * </pre>
     */
    public static class ArraySerializer extends BasicObjectSerializer {

        private final Class<?> componentType;

        private ArraySerializer(Class<?> componentType) {
            this.componentType = componentType;
        }

        public static ArraySerializer createArraySerializer(Class<?> componentType) {
            return new ArraySerializer(componentType);
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            int dimensions = dataInput.readByte();
            if (dimensions > 1) {
                throw new UnsupportedOperationException("Not implemented!");
            }

            ObjectSerializer sr = ctx.getConfig().forType(componentType);

            Object values = Array.newInstance(componentType, dataInput.readInt());
            int length = Array.getLength(values);
            for (int i = 0; i < length; i++) {
                Array.set(values, i, sr.getInstance(componentType, dataInput, ctx));
            }

            return values;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
            ObjectSerializer sr = ctx.getConfig().forType(componentType);
            int length = Array.getLength(writableObject);
            dataOutput.writeInt(length);

            for (int i = 0; i < length; i++) {
                sr.writeObject(dataOutput, Array.get(writableObject, i), ctx);
            }
        }

        @Override
        public boolean accept(Class<?> type) {
            return componentType.isAssignableFrom(type);
        }
    }

    /**
     * <pre>
     * ┌──────────────────────────────────────────┐
     * │ List                                     │
     * ├────────────────┬──────────────┬──────────┤
     * │ type_len: byte │ type: byte[] │ len: int │
     * ├────────────────┴──────────────┴──────────┤
     * │ v1 v2 ...                                │
     * └──────────────────────────────────────────┘
     * </pre>
     */
    public static class CollectionSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return Collection.class.isAssignableFrom(type);
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
            Collection<?> collection = (Collection<?>) writableObject;
            Object[] values = collection.toArray();

            if (values.length != 0) {
                Class<?> c = values[0].getClass();

                byte[] name = c.getName().getBytes();
                dataOutput.writeByte(name.length);
                dataOutput.write(name);
                dataOutput.writeInt(values.length);

                ObjectSerializer sr = ctx.getConfig().forType(c);
                for (Object o : values) {
                    sr.writeObject(dataOutput, o, ctx);
                }
            } else {
                dataOutput.writeByte(0);
            }
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            byte len = dataInput.readByte();
            byte[] name = new byte[len];
            Collection<Object> collection;
            Class<?> componentType = null;

            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                if (List.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type)) {
                    type = ArrayList.class;
                } else if (Set.class.isAssignableFrom(type)) {
                    type = HashSet.class;
                } else if (Queue.class.isAssignableFrom(type)) {
                    type = ArrayDeque.class;
                }
            }

            try {
                for (int i = 0; i < len; i++) {
                    name[i] = dataInput.readByte();
                }

                if (len > 0) {
                    componentType = Class.forName(new String(name));
                }
                //noinspection unchecked
                collection = (Collection<Object>) type.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new InvalidClassException(e.getMessage());

            } finally {
                for (int i = 0; i < len; i++) {
                    name[i] = 0;
                }
            }
            if (len > 0) {
                int length = dataInput.readInt();
                ObjectSerializer sr = ctx.getConfig().forType(componentType);
                for (int i = 0; i < length; i++) {
                    collection.add(sr.getInstance(componentType, dataInput, ctx));
                }
            }
            return collection;
        }
    }

    /**
     * <pre>
     * ┌────────────────────────────────────────────────────┐
     * │ Map                                                │
     * ├───────────────┬──────────────────┬─────────────────┤
     * │ key_len: byte │ key_type: byte[] │ value_len: byte │
     * ├───────────────┴────┬─────────────┼─────────────────┤
     * │ value_type: byte[] │ amount: int │ k1 v1 k2 v2 ... │
     * └────────────────────┴─────────────┴─────────────────┘
     * </pre>
     */
    public static class KeyValueSerializer extends BasicObjectSerializer {

        @Override
        public boolean accept(Class<?> type) {
            return Map.class.isAssignableFrom(type);
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
            Map<?, ?> map = (Map<?, ?>) writableObject;
            int size = map.size();
            Class<?> keyType;
            Class<?> valueType;
            byte[] name = new byte[0];
            int nameLength = 0;

            if (size == 0) {
                dataOutput.writeByte(0);
                dataOutput.writeByte(0);
                dataOutput.writeInt(0);
                return;
            }
            try {
                Object[] keys = map.keySet().toArray();
                Object[] values = map.values().toArray();

                keyType = keys[0].getClass();
                valueType = values[0].getClass();

                name = keyType.getName().getBytes();
                nameLength = name.length;
                dataOutput.writeByte(nameLength);
                dataOutput.write(name);

                name = valueType.getName().getBytes();
                nameLength = name.length;
                dataOutput.writeByte(nameLength);
                dataOutput.write(name);

                dataOutput.writeInt(size);
                ObjectSerializer srK = ctx.getConfig().forType(keyType);
                ObjectSerializer srV = ctx.getConfig().forType(valueType);
                if (srK == null || srV == null) {
                    throw new IllegalArgumentException("Key or Value type can not be serialized!");
                }

                for (int i = 0; i < size; i++) {
                    srK.writeObject(dataOutput, keys[i], ctx);
                    srV.writeObject(dataOutput, values[i], ctx);
                }
            } finally {
                for (int i = 0; i < nameLength; i++) {
                    name[i] = 0;
                }
            }
        }

        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            Map<Object, Object> map;
            int size;
            Class<?> keyType;
            Class<?> valueType;
            ObjectSerializer srK, srV;

            try {
                keyType = getType(dataInput, ctx);
                valueType = getType(dataInput, ctx);

                if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                    type = HashMap.class;
                }

                //noinspection unchecked
                map = (Map<Object, Object>) type.getDeclaredConstructor().newInstance();
                size = dataInput.readInt();
                if (size == 0) {
                    return map;
                }
                srK = ctx.getConfig().forType(keyType);
                srV = ctx.getConfig().forType(valueType);

                if (srK == null || srV == null) {
                    throw new IllegalArgumentException("Key or Value type can not be serialized!");
                }
                for (int i = 0; i < size; i++) {
                    map.put(srK.getInstance(keyType, dataInput, ctx), srV.getInstance(valueType, dataInput, ctx));
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            return map;
        }

        private Class<?> getType(DataInput dataInput, OSerializationContext ctx)
                throws IOException, ClassNotFoundException {
            int nameLength = dataInput.readByte();
            if (nameLength == 0) return null;

            byte[] name = new byte[nameLength];
            for (int i = 0; i < nameLength; i++) {
                name[i] = dataInput.readByte();
            }

            return Class.forName(new String(name));
        }
    }


    @Deprecated
    public static class MultiDimensionArraySerializer extends BasicObjectSerializer {

        private final Class<?> baseType;
        private final Class<?> componentType;

        private final int dimensions;

        public MultiDimensionArraySerializer(Class<?> baseType) {
            this.baseType = baseType;

            Class<?> type = baseType;
            int amount = 1;
            while ((type = type.getComponentType()).isArray()) {
                amount++;
            }
            this.componentType = type;
            this.dimensions = amount;
        }

        @Override
        public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException {
            int[] size = new int[dimensions];

            Object last = writableObject;
            for (int i = 0; i < dimensions; i++) {
                if (last == null) {
                    size[i] = 0;
                    continue;
                }
                size[i] = Array.getLength(last);
                if (size[i] == 0) {
                    last = null;
                } else last = Array.get(last, 0);
            }

            dataOutput.writeByte(dimensions);
            for (int j : size) {
                dataOutput.writeInt(j);
            }

            ObjectSerializer sr = ctx.getConfig().forType(componentType);
            int len = Array.getLength(writableObject);
            writeArray(dataOutput, writableObject, len, sr, ctx);
        }

        private void writeArray(DataOutput dataOutput, Object array, int length, ObjectSerializer sr,
                                OSerializationContext ctx) throws IOException {
            for (int i = 0; i < length; i++) {
                Object value = Array.get(array, i);
                if (value.getClass().isArray()) {
                    writeArray(dataOutput, value, Array.getLength(value), sr, ctx);
                } else {
                    sr.writeObject(dataOutput, value, ctx);
                }
            }
        }


        @Override
        public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
            int sDimensions = dataInput.readByte();
            if (sDimensions != dimensions) {
                throw new IllegalArgumentException("Serialized dimensions mismatch");
            }

            int[] size = new int[dimensions];
            for (int i = 0; i < size.length; i++) {
                size[i] = dataInput.readInt();
            }

            Object array = Array.newInstance(componentType, size);
            readArray(array, dataInput, size, ctx, ctx.getConfig().forType(componentType));
            return array;

        }

        private void readArray(Object array, DataInput dataInput, int[] size,
                               OSerializationContext ctx, ObjectSerializer sr) throws IOException {
            Object base;
            if (Array.getLength(array) == 0) return;
            while ((base = Array.get(array, 0)).getClass().isArray()) {
                if (base.getClass().getComponentType().isArray()
                        && base.getClass().getComponentType().getComponentType() == componentType) {
                    break;
                }
            }

            for (int i = 0; i < size[size.length - 2]; i++) {
                Object next = Array.get(base, i);
                for (int j = 0; j < Array.getLength(next); j++) {
                    Array.set(next, j, sr.getInstance(componentType, dataInput, ctx));
                }
            }
        }

        public Class<?> getBaseType() {
            return baseType;
        }

        @Override
        public boolean accept(Class<?> type) {
            return type.getName().startsWith("[[");
        }
    }
}

