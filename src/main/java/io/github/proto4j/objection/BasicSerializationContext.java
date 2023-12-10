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

import io.github.proto4j.objection.model.OClass;
import io.github.proto4j.objection.model.OField;

public class BasicSerializationContext implements OSerializationContext {

    private final OClass<?> classInfo;
    private final OField reference;
    private final OSharedConfiguration config;

    public BasicSerializationContext(OClass<?> classInfo, OField reference, OSharedConfiguration config) {
        this.classInfo = classInfo;
        this.reference = reference;
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OClass<?> getClassInfo() {
        return classInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OSharedConfiguration getConfig() {
        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OField getReference() {
        return reference;
    }
}
