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

package io.github.proto4j.objection; //@date 27.08.2022

import io.github.proto4j.objection.model.OClass;
import io.github.proto4j.objection.model.OField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.util.Objects;

public class BasicMarshaller<V> extends AbstractMarshaller<V> {

    public BasicMarshaller() {
    }

    public BasicMarshaller(OSharedConfiguration configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OSharedConfiguration marshall(V value, DataOutput output) throws IOException, ReflectiveOperationException {
        Objects.requireNonNull(value);
        Objects.requireNonNull(output);

        ObjectSerializer sr = getConfiguration().forType(OClass.class);
        if (sr == null) {
            throw new InvalidClassException("No OClass serializer specified");
        }

        getConfiguration().addType(value.getClass());
        OClass<V> cls = OClass.klass(value, getConfiguration());

        Object o = Objects.requireNonNull(cls.getInstance());
        for (OField field : cls.getDeclaredFields()) {
            if (field.getValue() == null) {
                Field linked = field.getLinkedField();
                linked.setAccessible(true);
                field.setValue(linked.get(o));
            }
        }

        OSerializationContext ctx = new BasicSerializationContext(cls, null, getConfiguration());
        sr.writeObject(output, cls, ctx);
        return getConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OClass<V> unmarshall(DataInput input, OSharedConfiguration configuration) throws IOException {
        Objects.requireNonNull(input);
        Objects.requireNonNull(configuration);

        setConfiguration(configuration);
        ObjectSerializer sr = getConfiguration().forType(OClass.class);
        if (sr == null) {
            throw new InvalidClassException("No OClass serializer specified");
        }

        OSerializationContext ctx = new BasicSerializationContext(null, null, getConfiguration());
        //noinspection unchecked
        return (OClass<V>) sr.getInstance(OClass.class, input, ctx);
    }
}
