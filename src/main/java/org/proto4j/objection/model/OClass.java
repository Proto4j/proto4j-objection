package org.proto4j.objection.model;//@date 25.08.2022

import org.proto4j.objection.OReflection;
import org.proto4j.objection.OSharedConfiguration;
import org.proto4j.objection.annotation.Transient;
import org.proto4j.objection.serial.DefaultSharedConfiguration;

import java.lang.ref.SoftReference;
import java.lang.reflect.*;
import java.util.*;

/**
 * <h2>OClass Structure:</h2>
 * <p>
 * The serialized structure can be summarized to the following:
 * <pre>
 * ┌────────────────────────────────────────────────────────────────┐
 * │ Class                                                          │
 * ├───────────────┬────────────────┬──────────────┬────────────────┤
 * │ version: byte │ name_len: byte │ name: byte[] │ modifiers: int │
 * ├────────────┬──┴────────────────┼──────────────┴────────────────┤
 * │ clsid: int │ field_amount: int │ fields: byte[]                │
 * └────────────┴───────────────────┴───────────────────────────────┘
 * </pre>
 *
 * @param <T> the class type stored in this class
 */
public final class OClass<T> implements Type {

    private final Class<T>             type;
    private final int                  modifiers;
    private final int                  classId;
    private final OSharedConfiguration configuration;

    private transient volatile SoftReference<OClassInfo<T>> classInfoRef;
    private transient volatile SoftReference<T> instance;

    private OClass(Class<T> linkedClass, OSharedConfiguration configuration) {
        this(linkedClass, configuration, null);
    }

    private OClass(Class<T> linkedClass, OSharedConfiguration configuration, T value) {
        this.type          = Objects.requireNonNull(linkedClass);
        this.modifiers     = linkedClass.getModifiers();
        this.classId       = linkedClass.hashCode();
        this.configuration = Objects.requireNonNull(configuration);
        this.instance = value != null ? new SoftReference<>(value) : null;
        Objects.requireNonNull(classInfo());
    }

    public static <T> OClass<T> klass(Class<T> linkedClass) {
        return klass(linkedClass, new DefaultSharedConfiguration());
    }

    public static <T> OClass<T> klass(Class<T> linkedClass, OSharedConfiguration configuration) {
        return new OClass<>(linkedClass, configuration);
    }

    public static <T> OClass<T> klass(T value, OSharedConfiguration configuration) {
        //noinspection unchecked
        return new OClass<>((Class<T>)value.getClass(), configuration, value);
    }

    public Class<T> getType() {
        return type;
    }

    public boolean requiresInit() {
        return (modifiers & 0x0000_1000) != 0;
    }

    public String getSimpleName() {
        OClassInfo<T> info = classInfo();
        return info.name;
    }

    public byte[] getBufferedName() {
        OClassInfo<T> info   = classInfo();
        int           length = info.bufferedName.length;
        if (length == 0) {
            return new byte[0];
        }
        return Arrays.copyOf(info.bufferedName, length);
    }

    public byte getVersion() {
        OClassInfo<T> info = classInfo();
        return info.version == -1 ? 0 : info.version;
    }

    public int getClassId() {
        return classId;
    }

    public int getModifiers() {
        return modifiers;
    }

    public OField[] getDeclaredFields() {
        OClassInfo<T> info = classInfo();
        if (info.declaredFields.length == 0) {
            return new OField[0];
        }
        return Arrays.copyOf(info.declaredFields, info.declaredFields.length);
    }

    public OSharedConfiguration getConfiguration() {
        return this.configuration;
    }

    private OClassInfo<T> classInfo() {
        SoftReference<OClassInfo<T>> classInfo = this.classInfoRef;
        OClassInfo<T>                info;
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

        info.name         = type.getName();
        info.bufferedName = info.name.getBytes();
        createFields(getType());
        info.declaredConstructors = type.getConstructors();
        return info;
    }

    private void createFields(Class<?> type) {
        if (type.isEnum() || type.isInterface()) {
            return;
        }

        List<Field>   fields = getAnnotatedFields(type.getDeclaredFields());
        OClassInfo<T> info   = classInfo();

        info.declaredFields = new OField[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            info.declaredFields[i] = createField0(type, field);
        }
    }

    private List<Field> getAnnotatedFields(Field[] declaredFields) {
        if (declaredFields.length == 0) {
            return Collections.emptyList();
        }
        List<Field> fields = new LinkedList<>();
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

            fields.add(field);
        }
        return fields;
    }

    private OField createField0(Class<?> type, Field field) {
        return new OField(type, field);
    }

    public Constructor<?>[] getDeclaredConstructors() {
        OClassInfo<T> info   = classInfo();
        int           length = info.declaredConstructors.length;
        if (length == 0) {
            return new Constructor[0];
        }
        return Arrays.copyOf(info.declaredConstructors, length);
    }

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

    public OField getDeclaredField(String s) {
        for (OField field : getDeclaredFields()) {
            if (field.getName().equals(s)) {
                return field;
            }
        }
        return null;
    }

    @Override
    public String getTypeName() {
        return getType().getTypeName();
    }

    public T newInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance.get();
    }

    public Object getInstance() {
        return instance != null ? instance.get() : null;
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

    private static class OClassInfo<T> {
        volatile OField[]         declaredFields;

        // REVISIT: Changed declaredConstructors from typed version
        // to a more generic version. The generic type of this class
        // is remained for future usage.
        volatile Constructor<?>[] declaredConstructors;

        String name;
        byte[] bufferedName;
        byte   version;

        OClassInfo() {}
    }
}
