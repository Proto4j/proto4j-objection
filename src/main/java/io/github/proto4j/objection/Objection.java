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

import io.github.proto4j.objection.internal.DefaultSharedConfiguration;

import java.io.*;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Core functionalities of the {@code proto4j-objection} module are presented
 * in this class. Methods can be used to set up the serialization and de-
 * serialization process.
 * <p>
 * Version history with any crucial changes to the system is presented below:
 * <ul>
 *     <li>0.2.0: initial beta release</li>
 * </ul>
 *
 * @author MatrixEditor
 * @version 0.2.0
 */
public final class Objection {

    private Objection() {}

    /**
     * Creates a new generic {@link Marshaller} instance with the default
     * configuration values.
     *
     * @param <T> the type this marshaller should be serializing and de-serializing
     * @return a new generic {@link Marshaller} instance with the default
     *         configuration values.
     */
    public static <T> Marshaller<T> createMarshaller() {
        return createMarshaller(getDefaultConfiguration());
    }

    /**
     * Creates a new generic {@link Marshaller} instance with the given
     * configuration values.
     *
     * @param config the given configuration instance
     * @param <T> the type this marshaller should be serializing and de-serializing
     * @return a new generic {@link Marshaller} instance with the given
     *         configuration values.
     */
    public static <T> Marshaller<T> createMarshaller(OSharedConfiguration config) {
        return new BasicMarshaller<>(config);
    }

    /**
     * Creates a new {@link DataInput} instance from the given {@link InputStream}
     * object. The supplier can be used within {@link java.net.Socket} instances,
     * for example:
     * <pre>
     *     Socket socket = new Socket(...);
     *     DataInput input = Objection.createDataInput(socket::getInputStream());
     * </pre>
     *
     * @param supplier a {@link Supplier} of the resource stream
     * @return a new {@link DataInput} instance from the given {@link InputStream}
     *         object
     */
    public static DataInput createDataInput(Supplier<? extends InputStream> supplier) {
        return createDataInput(supplier.get());
    }

    /**
     * Creates a new {@link DataInput} instance from the given {@link InputStream}
     * object.
     *
     * @param inputStream the resource stream
     * @return a new {@link DataInput} instance from the given {@link InputStream}
     *         object
     */
    public static DataInput createDataInput(InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        return new DataInputStream(inputStream);
    }

    /**
     * Creates a new {@link DataOutput} instance from the given {@link OutputStream}
     * object. The supplier can be used within {@link java.net.Socket} instances,
     * for example:
     * <pre>
     *     Socket socket = new Socket(...);
     *     DataOutput output = Objection.createDataOutput(socket::getOutputStream());
     * </pre>
     *
     * @param supplier a {@link Supplier} of the resource output
     * @return a new {@link DataOutput} instance from the given {@link OutputStream}
     *         object
     */
    public static DataOutput createDataOutput(Supplier<? extends OutputStream> supplier) {
        return createDataOutput(supplier.get());
    }

    /**
     * Creates a new {@link DataOutput} instance from the given {@link OutputStream}
     * object.
     *
     * @param outputStream the resource output
     * @return a new {@link DataOutput} instance from the given {@link OutputStream}
     *         object
     */
    public static DataOutput createDataOutput(OutputStream outputStream) {
        Objects.requireNonNull(outputStream);
        return new DataOutputStream(outputStream);
    }

    /**
     * @return a new instance for the default serialization configuration.
     */
    public static OSharedConfiguration getDefaultConfiguration() {
        return new DefaultSharedConfiguration();
    }

}
