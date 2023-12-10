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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Objects;

/**
 * Used by an {@link Marshaller} object and registered in the {@link OSharedConfiguration}
 * instance. Objects of this class may be used to read and write the implemented schema.
 * It is recommended to register new serializers for the following types of classes:
 * <ul>
 *     <li>Raw Objects (Object.class)</li>
 *     <li>Generic self-implemented types</li>
 *     <li>Self-implemented classes</li>
 *     <li>In-Built complex types, for example Class.class</li>
 * </ul>
 *
 * @author MatrixEditor
 * @version 0.2.0
 */
public interface ObjectSerializer {

    /**
     * Writes the given object in a specific binary format to the given
     * {@link DataOutput} stream.
     *
     * @param dataOutput a wrapper for the underlying {@link java.io.OutputStream}.
     * @param writableObject the object to be written
     * @param ctx a general context object storing informational resources, such
     *         as the configuration with all registered {@link ObjectSerializer}
     *         instances.
     * @throws IOException if an error occurs while writing
     */
    void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx) throws IOException;

    /**
     * Reads data from the given stream and converts that into a qualified
     * object.
     *
     * @param type the class of the returned instance
     * @param dataInput a wrapper for the underlying {@link java.io.InputStream}.
     * @param ctx a general context object storing informational resources, such
     *         as the configuration with all registered {@link ObjectSerializer}
     *         instances.
     * @return an instance of the given type.
     * @throws IOException if an error occurs while reading
     */
    Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) throws IOException;

    /**
     * Reads data from the given stream and converts that into a qualified
     * object of type {@code T}.
     *
     * @param type the class of the returned instance
     * @param dataInput a wrapper for the underlying {@link java.io.InputStream}.
     * @param ctx a general context object storing informational resources, such
     *         as the configuration with all registered {@link ObjectSerializer}
     *         instances.
     * @param <T> the type of the returned instance
     * @return an instance of the given type.
     * @throws IOException if an error occurs while reading
     */
    default <T> T getTypeInstance(Class<T> type, DataInput dataInput, OSerializationContext ctx) throws IOException {
        if (!accept(Objects.requireNonNull(type))) {
            throw new InvalidClassException(type.getSimpleName() + " is not supported");
        }
        return type.cast(getInstance(type, dataInput, ctx));
    }

    /**
     * Returns whether this serializer can handle the given class type (perform
     * reading and writing).
     *
     * @param type the type to be read or written
     * @return true if this implementation can read/ write instances of this
     *         type, false otherwise.
     */
    boolean accept(Class<?> type);

}
