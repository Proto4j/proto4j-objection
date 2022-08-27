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
 * Every Object that is serialized contains different field declarations with
 * the following structure:
 * <pre>
 * ┌────────────────────────────────────────────────────────────┐
 * │ Field                                                      │
 * ├────────────┬────────────┬────────────────┬─────────────────┤
 * │ type: byte │ ord: short │ modifiers: int │ field_len: byte │
 * ├────────────┴──┬─────────┴────────────────┴─────────────────┤
 * │ field: byte[] │ value: byte[]                              │
 * └───────────────┴────────────────────────────────────────────┘
 * </pre>
 *
 * <pre>
 * ┌─────────────────────────────────────────────────────────────┐
 * │ Class                                                       │
 * ├────────────────┬──────────────┬────────────────┬────────────┤
 * │ name_len: byte │ name: byte[] │ modifiers: int │ clsid: int │
 * ├────────────────┴──────────────┴────────────────┴────────────┤
 * │                       fields: byte[]                        │
 * └─────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * @author MatrixEditor
 **/
package org.proto4j.objection;