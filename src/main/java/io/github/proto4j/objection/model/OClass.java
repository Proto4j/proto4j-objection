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

package io.github.proto4j.objection.model;//@date 25.08.2022

import io.github.proto4j.objection.OSharedConfiguration;
import io.github.proto4j.objection.annotation.Serialize;
import io.github.proto4j.objection.annotation.Transient;
import io.github.proto4j.objection.annotation.Version;
import io.github.proto4j.objection.internal.OReflection;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * <h3>OClass Type Wrapper</h3>
 * A wrapper class designed to make information gathering on types that should
 * be serialized or de-serialized much easier. Additionally, this API uses the
 * {@link #newInstance()} for creating new instances and {@link #getInstance()}
 * for getting already linked instances.
 * <p>
 * The basic structure of binary serialized {@link OClass} objects is the
 * following:
 * <pre>
 * +------------------------------------------------+
 * | OClass of type T                               |
 * +---------------+----------------+---------------+
 * | version: byte | name_len: byte | name: byte[]  |
 * +---------------++---------+-----+---------------+
 * | modifiers: int | id: int | field_count: int    |
 * +----------------+---------+---------------------+
 * | fields: OField[]                               |
 * | +--------------------------------------------+ |
 * | | Field1:                                    | |
 * | +------------+---------------+---------------+ |
 * | | type: byte | version: byte | namelen: byte | |
 * | +------------+-+-------------+---------------+ |
 * | | name: byte[] | value: byte[]               | |
 * | +--------------+-----------------------------+ |
 * | ...                                            |
 * +------------------------------------------------+
 * </pre>
 *
 * @param <T> the class type stored in this class
 * @author MatrixEditor
 * @version 0.2.0
 */
public final class OClass<T> implements Type {

    /**
     * The actual type linked to this instance.
     */
    private final Class<T> type;

    /**
     * The class modifiers used for checksum identification.
     */
    private final int modifiers;

    /**
     * The class ID is computed with the hashcode of the linked type.
     */
    private final int classId;

    /**
     * Additional configuration link here to make this configuration accessible
     * in the serialization process in earlier versions.
     */
    private final OSharedConfiguration configuration;

    private transient volatile SoftReference<OClassInfo<T>> classInfoRef;
    private transient volatile SoftReference<T> instance;

    /**
     * Create an OClass from the given type and configuration. Note the
     * configuration is not used anymore in this class. This constructor is
     * called via static factory methods and used in the de-serialization
     * process.
     *
     * @param linkedClass the linked type
     * @param configuration the serialization configuration
     */
    private OClass(Class<T> linkedClass, OSharedConfiguration configuration) {
        this(linkedClass, configuration, null);
    }

    /**
     * Create an OClass from the given type, configuration and instance. Note the
     * configuration is not used anymore in this class. This constructor is
     * called via static factory methods and used in the serialization
     * process.
     *
     * @param linkedClass the linked type
     * @param configuration the serialization configuration
     * @param value an instance of the given type
     */
    private OClass(Class<T> linkedClass, OSharedConfiguration configuration, T value) {
        checkType(linkedClass);
        this.type = Objects.requireNonNull(linkedClass);
        this.modifiers = linkedClass.getModifiers();
        this.classId = linkedClass.hashCode();
        this.configuration = configuration;
        this.instance = value != null ? new SoftReference<>(value) : null;
        Objects.requireNonNull(classInfo());
    }

    /**
     * Creates a new {@link OClass} instance for the given type.
     *
     * @param linkedClass the linked class type
     * @param <T> the linked type
     * @return a new {@link OClass} instance for the given type.
     */
    public static <T> OClass<T> klass(Class<T> linkedClass) {
        return klass(linkedClass, null);
    }

    /**
     * Creates a new {@link OClass} instance for the given type with an instance
     * of the {@link OSharedConfiguration} class.
     *
     * @param linkedClass the linked class type
     * @param configuration deprecated: leave this parameter null
     * @param <T> the linked type
     * @return a new {@link OClass} instance for the given type.
     */
    public static <T> OClass<T> klass(Class<T> linkedClass, OSharedConfiguration configuration) {
        return new OClass<>(linkedClass, configuration);
    }

    /**
     * Creates a new {@link OClass} instance for the given type instance.
     *
     * @param value the linked class type instance
     * @param configuration deprecated: leave this parameter null
     * @param <T> the linked type
     * @return a new {@link OClass} instance for the given type.
     */
    public static <T> OClass<T> klass(T value, OSharedConfiguration configuration) {
        //noinspection unchecked
        return new OClass<>((Class<T>) value.getClass(), configuration, value);
    }

    /**
     * @return the linked class type.
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * @return the linked class name (full name)
     */
    public String getName() {
        OClassInfo<T> info = classInfo();
        return info.name;
    }

    /**
     * @return the linked class name as byte array
     */
    public byte[] getBufferedName() {
        OClassInfo<T> info = classInfo();
        int length = info.bufferedName.length;
        if (length == 0) {
            return new byte[0];
        }
        return Arrays.copyOf(info.bufferedName, length);
    }

    /**
     * @return if the linked class is annotated with
     */
    public byte getVersion() {
        OClassInfo<T> info = classInfo();
        return info.version == -1 ? Version.INITIAL_VERSION : info.version;
    }

    /**
     * @return the hashcode of the linked class
     */
    public int getClassId() {
        return classId;
    }

    /**
     * @return the same as {@link #getType()}.getModifiers()
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * @return an array of all serializable or de-serializable fields.
     */
    public OField[] getDeclaredFields() {
        OClassInfo<T> info = classInfo();
        if (info.declaredFields.length == 0) {
            return new OField[0];
        }
        return Arrays.copyOf(info.declaredFields, info.declaredFields.length);
    }

    /**
     * @return the given configuration instance.
     */
    public OSharedConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * @return an array of all usable constructors.
     */
    public Constructor<?>[] getDeclaredConstructors() {
        OClassInfo<T> info = classInfo();
        int length = info.declaredConstructors.length;
        if (length == 0) {
            return new Constructor[0];
        }
        return Arrays.copyOf(info.declaredConstructors, length);
    }

    /**
     * @return the default T.$init() constructor.
     */
    public Constructor<T> getDefaultConstructor() {
        OClassInfo<T> info = classInfo();
        for (Constructor<?> con : info.declaredConstructors) {
            if (con.getParameterCount() == 0) {
                // We already know that there are only Constructor<T> types
                // stored in the declaredConstructors array.
                //noinspection unchecked
                return (Constructor<T>) con;
            }
        }
        throw new NullPointerException("There is no default constructor for " + info.name);
    }

    /**
     * Returns the {@link OField} instance named with the given {@link String}.
     *
     * @param s the field's name
     * @return the internal {@link OField} instance or null if no field with the
     *         given name is stored.
     */
    public OField getDeclaredField(String s) {
        for (OField field : getDeclaredFields()) {
            if (field.getName().equals(s)) {
                return field;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return getType().getTypeName();
    }

    /**
     * @return creates a new instance with the loaded values from a binary
     *         stream.
     */
    public T newInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance.get();
    }

    /**
     * @return the linked instance converted to a raw {@link Object}.
     */
    public Object getInstance() {
        return instance != null ? instance.get() : null;
    }

    private OClassInfo<T> classInfo() {
        SoftReference<OClassInfo<T>> classInfo = this.classInfoRef;
        OClassInfo<T> info;
        if (classInfo != null && (info = classInfo.get()) != null) {
            return info;
        }
        return newClassInfo();
    }

    private OClassInfo<T> newClassInfo() {
        OClassInfo<T> info = new OClassInfo<>();
        this.classInfoRef = new SoftReference<>(info);
        if (type == null) {
            return info;
        }

        info.name = type.getName();
        info.bufferedName = info.name.getBytes();
        info.declaredConstructors = type.getConstructors();

        Optional<Version> version = OReflection.getAnnotation(getType(), Version.class);
        version.ifPresent(v -> info.version = v.value());

        createFields(getType());
        return info;
    }

    private void createFields(Class<?> type) {
        if (type.isEnum() || type.isInterface()) {
            return;
        }

        List<Field> fields = getAnnotatedFields(type.getDeclaredFields());
        while (type != Object.class) {
            type = type.getSuperclass();
            if (type.isInterface()) break;
            fields.addAll(getAnnotatedFields(type.getDeclaredFields()));
        }
        OClassInfo<T> info = classInfo();

        info.declaredFields = new OField[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            info.declaredFields[i] = createField0(field);
        }
    }

    private List<Field> getAnnotatedFields(Field[] declaredFields) {
        if (declaredFields.length == 0) {
            return Collections.emptyList();
        }
        List<Field> fields = new LinkedList<>();
        // Direct access is needed here because this method is called within
        // the newClassInfo() method. The returned value is never null, because
        // this method is called after all fields were set.
        OClassInfo<T> info = classInfoRef.get();

        for (Field field : declaredFields) {
            if (field.isSynthetic() || field.isEnumConstant()) {
                continue;
            }

            int mods = field.getModifiers();
            if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
                continue;
            }

            if (OReflection.isPresent(field, Transient.class)) {
                continue;
            }

            Optional<Version> version = OReflection.getAnnotation(field, Version.class);
            if (version.isPresent() && info != null) {
                if (version.get().value() > info.version) {
                    continue;
                }
            }

            fields.add(field);
        }
        return fields;
    }

    private OField createField0(Field field) {
        return new OField(this, field);
    }

    private void createInstance() {
        T value = null;
        try {
            value = getDefaultConstructor().newInstance();
            for (OField field : getDeclaredFields()) {
                Field f0 = field.getLinkedField();
                f0.setAccessible(true);
                f0.set(value, field.getValue());
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        } finally {
            instance = new SoftReference<>(value);
        }
    }

    private void checkType(Class<T> type) {
        if (Serializable.class.isAssignableFrom(type)
                || OReflection.isPresent(type, Serialize.class)) {
            return;
        }
        throw new IllegalArgumentException("Class is not serializable");
    }

    private static class OClassInfo<T> {
        volatile OField[] declaredFields;

        // REVISIT: Changed declaredConstructors from typed version
        // to a more generic version. The generic type of this class
        // is remained for future usage.
        volatile Constructor<?>[] declaredConstructors;

        String name;
        byte[] bufferedName;
        byte version = Version.INITIAL_VERSION;

        OClassInfo() {}
    }
}
