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

package io.github.proto4j.objection; //@date 25.08.2022

import java.nio.charset.StandardCharsets;

/**
 * The base class for configuration based serialization. For most implementations
 * instances of this class should contain a list of serializers and a map of
 * classes that can be read/written to or from any source.
 *
 * @author MatrixEditor
 * @version 0.2.0
 */
public interface OSharedConfiguration {

    ObjectSerializer forType(Class<?> type);

    Class<?> forName(String name);

    default Class<?> forName(byte[] name) throws ClassNotFoundException {
        return forName(new String(name, StandardCharsets.UTF_8));
    }

    boolean isRegistered(String name);

    default boolean isRegistered(byte[] name) {
        return isRegistered(new String(name, StandardCharsets.UTF_8));
    }

    void addType(Class<?> cls);

    void addSerializer(ObjectSerializer serializer);
}
