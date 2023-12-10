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

package io.github.proto4j.objection;//@date 27.08.2022

import io.github.proto4j.objection.model.OClass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The base class for objects that are used to serialize and de-serialize
 * specific objects of type '{@code V}'. A {@link BasicMarshaller} can be retrieved
 * by calling {@link Objection#createMarshaller()}.
 * <p>
 * An abstract version of this interface is provided through the {@link AbstractMarshaller}
 * class. The basic usage of this class should be:
 * <pre>
 *     Marshaller<?> m = Objection.createMarshaller(baseConfig);
 *
 *     // get changed configuration after writing
 *     OSharedConfiguration config = m.marshall(value, stream);
 *     // and use that config when de-serializing
 *     ? value2 = m.unmarshall(stream, config);
 * </pre>
 *
 * @param <V> the type of objects to read/write
 * @see BasicMarshaller
 * @see AbstractMarshaller
 */
public interface Marshaller<V> {

    /**
     * Tries to write a binary representation of the given instance of type
     * {@code V} to the {@link DataOutput} object.
     *
     * @param value the type instance
     * @param output the destination stream wrapper
     * @return a modified version of the initial configuration
     * @throws IOException                  if an error while writing occurs
     * @throws ReflectiveOperationException if values could not be fetched dynamically
     */
    OSharedConfiguration marshall(V value, DataOutput output) throws IOException, ReflectiveOperationException;

    /**
     * Tries to read the binary representation of type {@code V} from the given
     * {@link DataInput} object with a {@link OSharedConfiguration}.
     *
     * @param input the input source
     * @param configuration an object containing all registered and usable
     *         serializers and readable types.
     * @return the {@link OClass} wrapper for the loaded instance.
     * @throws IOException if an error occurs while reading
     */
    // ENHANCEMENT: Try to overload this method in the future, so it can be
    // called without the need of providing the configuration instance.
    OClass<V> unmarshall(DataInput input, OSharedConfiguration configuration) throws IOException;

    /**
     * Alternative version for {@link #unmarshall(DataInput, OSharedConfiguration)}.
     *
     * @param input the input source
     * @param configuration an object containing all registered and usable
     *         serializers and readable types.
     * @return the loaded instance.
     * @throws IOException                  if an error occurs while reading
     * @throws ReflectiveOperationException if the values could not be applied
     *                                      to the new type instance.
     * @throws ClassCastException           if the wrong class was de-serialized
     */
    default V getInstance(DataInput input, OSharedConfiguration configuration) throws IOException,
            ReflectiveOperationException {
        OClass<V> cls = unmarshall(input, configuration);
        return cls.newInstance();
    }

}
