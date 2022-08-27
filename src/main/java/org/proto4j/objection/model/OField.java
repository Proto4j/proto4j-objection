package org.proto4j.objection.model;//@date 25.08.2022

import org.proto4j.objection.OReflection;
import org.proto4j.objection.annotation.Version;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * <pre>
 * ┌──────────────────────────────────────────────┐
 * │ Field                                        │
 * ├────────────┬───────────────┬─────────────────┤
 * │ type: byte │ version: byte │ field_len: byte │
 * ├────────────┴──┬────────────┴─────────────────┤
 * │ field: byte[] │ value: byte[]                │
 * └───────────────┴──────────────────────────────┘
 * </pre>
 */
public final class OField implements Destroyable {

    private final Field    reference;
    private final Class<?> parent;

    private transient volatile SoftReference<OFieldInfo> fieldInfoRef;
    public static class OFieldInfo {
        volatile String name;
        volatile byte fieldType;
        volatile byte version;
        volatile Object value;
    }

    public OField(Class<?> type, Field ref) {
        this.parent = Objects.requireNonNull(type);
        this.reference    = Objects.requireNonNull(ref);
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

    public byte getVersion() {
        OFieldInfo info = fieldInfo();
        if (info == null) {
            return 0;
        }
        return info.version;
    }

    public String getName() {
        OFieldInfo info = fieldInfo();
        if (info == null) {
            return "";
        }
        return info.name;
    }

    /**
     * This field is the final class instance of the mapped field object.
     *
     * @return the type of the linked field.
     */
    public Field getLinkedField() {
        return this.reference;
    }

    public Class<?> getLinkedFieldType() {
        return getLinkedField().getType();
    }

    public void setValue(Object value) {
        OFieldInfo info = fieldInfo();
        info.value = value;
    }

    public Object getValue() {
        OFieldInfo info = fieldInfo();
        return info.value;
    }

    public Class<?> getParent() {
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

        Optional<Version> version = OReflection.getAnnotation(getLinkedField(), Version.class);
        info.version = version.map(Version::value).orElse((byte) 0);
        return info;
    }

    @Override
    public void destroy() {
    }
}
