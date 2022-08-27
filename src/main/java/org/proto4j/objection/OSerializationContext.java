package org.proto4j.objection;//@date 26.08.2022

import org.proto4j.objection.model.OClass;
import org.proto4j.objection.model.OField;

public interface OSerializationContext {

    OClass<?> getClassInfo();

    OSharedConfiguration getConfig();

    OField getReference();
}
