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

import io.github.proto4j.objection.annotation.Version;
import io.github.proto4j.objection.internal.OReflection;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * Instances of the {@code OField} represents single field objects stored in
 * a specific {@link OClass}. It provides information about, and dynamic access
 * to, a single field of an {@link OClass}.
 * <p>
 * The binary structure of an OField instance can be summarized to the
 * following:
 * <pre>
 *  +--------------------------------------------+
 *  | Field1:                                    |
 *  +------------+---------------+---------------+
 *  | type: byte | version: byte | namelen: byte |
 *  +------------+-+-------------+---------------+
 *  | name: byte[] | value: byte[]               |
 *  +--------------+-----------------------------+
 * </pre>
 *
 * @author MatrixEditor
 * @version 0.2.0
 * @see OClass
 */
public final class OField {

    /**
     * The {@code OField's} java reflect instance.
     */
    private final Field reference;

    /**
     * The Class instance describing what type this field is stored in.
     */
    private final OClass<?> parent;

    private final transient SoftReference<OFieldInfo> fieldInfoRef;

    /**
     * Creates a new OField instance from the given base type and {@link Field}
     * reference.
     *
     * @param type the Class instance describing what type this field is stored in.
     * @param ref the {@code OField's} java reflect instance
     */
    public OField(OClass<?> type, Field ref) {
        this.parent = Objects.requireNonNull(type);
        this.reference = Objects.requireNonNull(ref);
        this.fieldInfoRef = new SoftReference<>(fieldInfo());
    }

    /**
     * @return the type specification of this field
     */
    public byte getFieldType() {
        OFieldInfo info = fieldInfo();
        if (info == null) {
            return 0;
        }
        return info.fieldType;
    }

    /**
     * @return the correlating {@link OFieldType} instance.
     */
    public OFieldType getOFieldType() {
        return OFieldType.valueOf(getFieldType());
    }

    /**
     * @return the applied field version
     */
    public byte getVersion() {
        OFieldInfo info = fieldInfo();
        if (info == null) {
            return 0;
        }
        return info.version;
    }

    /**
     * @return the field's name.
     */
    public String getName() {
        OFieldInfo info = fieldInfo();
        if (info == null) {
            return "";
        }
        return info.name;
    }

    /**
     * This field is the field class instance of the mapped field object.
     *
     * @return the type of the linked field.
     */
    public Field getLinkedField() {
        return this.reference;
    }

    /**
     * Returns a Class object that identifies the declared type for the field.
     *
     * @return a Class object identifying the declared type of the field
     */
    public Class<?> getLinkedFieldType() {
        return fieldInfo().type;
    }

    /**
     * Returns the value of the field represented by this {@link OField}.
     *
     * @return the value of the field
     */
    public Object getValue() {
        OFieldInfo info = fieldInfo();
        if (info.value instanceof OClass) {
            return ((OClass<?>) info.value).newInstance();
        }
        return info.value;
    }

    /**
     * Sets the field represented by this Field object on the specified object
     * argument to the specified new value.
     *
     * @param value the new value for the field
     */
    public void setValue(Object value) {
        OFieldInfo info = fieldInfo();
        info.value = value;
    }

    /**
     * @return an {@code Class} object where this {@link OField} is located in.
     */
    public OClass<?> getParent() {
        return parent;
    }

    private OFieldInfo fieldInfo() {
        SoftReference<OFieldInfo> fieldInfo = this.fieldInfoRef;
        OFieldInfo info;
        if (fieldInfo != null && (info = fieldInfo.get()) != null) {
            return info;
        }
        return newFieldInfo();
    }

    private OFieldInfo newFieldInfo() {
        OFieldInfo info = new OFieldInfo();
        if (getLinkedField() == null) {
            return info;
        }

        info.name = getLinkedField().getName();
        info.fieldType = OFieldType.wrap(getLinkedField());
        info.type = getLinkedField().getType();

        Optional<Version> version = OReflection.getAnnotation(getLinkedField(), Version.class);
        info.version = version.map(Version::value).orElse((byte) 0);

        getLinkedField().setAccessible(true);
        if (getParent().getInstance() != null) {
            try {
                info.value = getLinkedField().get(getParent().getInstance());
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(
                        "Could not read value for OField(" + info.name + "); " + e);
            }
        }

        if (getParent().getConfiguration().forType(info.type) == null) {
            if (getParent().getInstance() != null) {
                info.value = OClass.klass(info.value, getParent().getConfiguration());
            } else {
                info.value = OClass.klass(info.type, getParent().getConfiguration());
            }
            info.type = OClass.class;
        }


        return info;
    }

    private static class OFieldInfo {
        volatile String name;
        volatile byte fieldType;
        volatile byte version;
        Object value;
        volatile Class<?> type;

        public OFieldInfo() {
        }
    }

}
