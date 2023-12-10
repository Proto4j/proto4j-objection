package io.github.proto4j.test.objection.model; //@date 27.08.2022

import io.github.proto4j.objection.annotation.Serialize;

@Serialize
public class Primitives {

    private int i;

    // standard Java serialization keyword to prevent this variable from
    // being serialized.
    private transient long notUsed;

    private float f;
    private double d;
    private char c;
    private byte b;
    private short s;
    private long l;

    public Primitives() {
    }

    public Primitives(int i, float f, double d, char c, byte b, short s, long l) {
        this.i = i;
        this.f = f;
        this.d = d;
        this.c = c;
        this.b = b;
        this.s = s;
        this.l = l;
    }

    @Override // generated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Primitives that = (Primitives) o;

        if (i != that.i) return false;
        if (Float.compare(that.f, f) != 0) return false;
        if (Double.compare(that.d, d) != 0) return false;
        if (c != that.c) return false;
        if (b != that.b) return false;
        if (s != that.s) return false;
        return l == that.l;
    }

}
