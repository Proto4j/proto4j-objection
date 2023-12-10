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

package io.github.proto4j.objection.internal; //@date 26.08.2022

import io.github.proto4j.objection.BasicSharedConfiguration;
import io.github.proto4j.objection.ObjectSerializer;
import io.github.proto4j.objection.serial.*;

import java.util.Objects;

public class DefaultSharedConfiguration extends BasicSharedConfiguration {

    public DefaultSharedConfiguration() {
        getSerializers().add(new NumberSerializer.ByteSerializer());
        getSerializers().add(new NumberSerializer.CharacterSerializer());
        getSerializers().add(new NumberSerializer.DoubleSerializer());
        getSerializers().add(new NumberSerializer.FloatSerializer());
        getSerializers().add(new NumberSerializer.LongSerializer());
        getSerializers().add(new NumberSerializer.ShortSerializer());
        getSerializers().add(new NumberSerializer.IntegerSerializer());
        getSerializers().add(new OClassSerializer());
        getSerializers().add(new OFieldSerializer());
        getSerializers().add(new StringSerializer());
        getSerializers().add(new SequenceSerializer.CollectionSerializer());
        getSerializers().add(new SequenceSerializer.KeyValueSerializer());
    }

    @Override
    public ObjectSerializer forType(Class<?> type) {
        ObjectSerializer sr = super.forType(type);
        if (sr == null && type.isArray()) {
            if (!type.getComponentType().isArray()) {
                return SequenceSerializer.ArraySerializer.createArraySerializer(type.getComponentType());
            } else throw new UnsupportedOperationException("MultiArrays not implemented");
        }
        return sr;
    }

    public synchronized void addSerializer(ObjectSerializer serializer) {
        Objects.requireNonNull(serializer);
        getSerializers().add(serializer);
    }

    public synchronized void addType(Class<?> type) {
        Objects.requireNonNull(type);
        getRegisteredClasses().put(type.getName(), type);
    }

}
