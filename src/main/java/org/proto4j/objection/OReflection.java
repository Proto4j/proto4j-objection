package org.proto4j.objection; //@date 26.08.2022

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.Optional;

public final class OReflection {

    private OReflection() {}

    public static <A extends Annotation> Optional<A> getAnnotation(AnnotatedElement element, Class<A> cls)
            throws NullPointerException {
        Objects.requireNonNull(element);
        Objects.requireNonNull(cls);

        A value = element.getAnnotation(cls);
        return Optional.ofNullable(value);
    }

    public static <A extends Annotation> boolean isPresent(AnnotatedElement element, Class<A> cls)
            throws NullPointerException {
        return getAnnotation(element, cls).isPresent();
    }
}
