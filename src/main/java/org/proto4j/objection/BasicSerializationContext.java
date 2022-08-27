package org.proto4j.objection; //@date 26.08.2022

import org.proto4j.objection.model.OClass;
import org.proto4j.objection.model.OField;

public class BasicSerializationContext implements OSerializationContext {

    private final OClass<?> classInfo;
    private final OField reference;
    private final OSharedConfiguration config;

    public BasicSerializationContext(OClass<?> classInfo, OField reference, OSharedConfiguration config) {
        this.classInfo = classInfo;
        this.reference = reference;
        this.config    = config;
    }

    @Override
    public OClass<?> getClassInfo() {
        return classInfo;
    }

    @Override
    public OSharedConfiguration getConfig() {
        return config;
    }

    @Override
    public OField getReference() {
        return reference;
    }
}
