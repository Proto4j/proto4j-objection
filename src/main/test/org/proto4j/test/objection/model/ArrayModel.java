package org.proto4j.test.objection.model; //@date 27.08.2022

import org.proto4j.objection.annotation.Serialize;
import org.proto4j.objection.annotation.Transient;

import java.util.Arrays;

@Serialize
public class ArrayModel {

    // Using the Objection-Annotation @Transient to prevent this field from
    // being serialized.
    @Transient
    private Object[] values;

    private int[] i;
    private String[] s;

    public ArrayModel(int[] i, String[] s) {
        this.i = i;
        this.s = s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayModel that = (ArrayModel) o;

        if (!Arrays.equals(i, that.i)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(s, that.s, String::compareTo);
    }

}
