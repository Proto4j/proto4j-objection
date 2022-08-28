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
 * {@link org.proto4j.objection.model.OClass} instance. It contains all relevant
 * information for the serialization process.
 *
 * In general, primitive types directly written to the stream as described in
 * the {@link org.proto4j.objection.serial.NumberSerializer} class info. Yet, it
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
 * to the related {@link org.proto4j.objection.ObjectSerializer} implementation.
 *
 * @author MatrixEditor
 * @version 0.2.0
 * @see org.proto4j.objection.Objection
 * @see org.proto4j.objection.ObjectSerializer
 * @see org.proto4j.objection.Marshaller
 * @see org.proto4j.objection.model.OClass
 **/
package org.proto4j.objection;