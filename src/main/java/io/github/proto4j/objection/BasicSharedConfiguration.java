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

package io.github.proto4j.objection; //@date 26.08.2022

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BasicSharedConfiguration implements OSharedConfiguration {

    private final ConcurrentMap<String, Class<?>> registeredClasses = new ConcurrentHashMap<>();
    private final List<ObjectSerializer> serializers = new LinkedList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> forName(String name) {
        Objects.requireNonNull(name);

        if (!isRegistered(name)) {
            return null;
        }
        return registeredClasses.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectSerializer forType(Class<?> type) {
        Objects.requireNonNull(type);

        for (ObjectSerializer serializer : serializers) {
            if (serializer.accept(type)) {
                return serializer;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegistered(String name) {
        return registeredClasses.containsKey(name);
    }

    public ConcurrentMap<String, Class<?>> getRegisteredClasses() {
        return registeredClasses;
    }

    public List<ObjectSerializer> getSerializers() {
        return serializers;
    }
}
