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

/**
 * <h1>Module Description:</h1>
 * This module of the {@code proto4j} project is written to provide a fast
 * and reliable object-serialization library. The structure of a serialized
 * object and its contents is described below.
 * <p>
 * <b>Important:</b> There is an option to check if the serialized class
 * contains unsafe operations which is highly recommended to use.
 *
 * <h2>Object Serialization Structure</h2>
 * Every Object that should be serialized is wrapped into an
 * {@link io.github.proto4j.objection.model.OClass} instance. It contains all relevant
 * information for the serialization process.
 * <p>
 * In general, primitive types directly written to the stream as described in
 * the {@link io.github.proto4j.objection.serial.NumberSerializer} class info. Yet, it
 * is not possible to read multidimensional arrays, but Collections, Maps and
 * simple arrays.The basic binary structure is the following:
 * <pre>
 * +------------------------------------------------+
 * | OClass of type T                               |
 * +---------------+----------------+---------------+
 * | version: byte | name_len: byte | name: byte[]  |
 * +---------------++---------+-----+---------------+
 * | modifiers: int | id: int | field_count: int    |
 * +----------------+---------+---------------------+
 * | fields: OField[]                               |
 * | +--------------------------------------------+ |
 * | | Field1:                                    | |
 * | +------------+---------------+---------------+ |
 * | | type: byte | version: byte | namelen: byte | |
 * | +------------+-+-------------+---------------+ |
 * | | name: byte[] | value: byte[]               | |
 * | +--------------+-----------------------------+ |
 * | ...                                            |
 * +------------------------------------------------+
 * </pre>
 * For a more detailed review of each individual value structure please refer
 * to the related {@link io.github.proto4j.objection.ObjectSerializer} implementation.
 *
 * @author MatrixEditor
 * @version 0.2.0
 * @see io.github.proto4j.objection.Objection
 * @see io.github.proto4j.objection.ObjectSerializer
 * @see io.github.proto4j.objection.Marshaller
 * @see io.github.proto4j.objection.model.OClass
 **/
package io.github.proto4j.objection;