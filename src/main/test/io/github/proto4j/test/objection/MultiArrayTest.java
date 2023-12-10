package io.github.proto4j.test.objection; //@date 28.08.2022

import io.github.proto4j.objection.ObjectSerializer;
import io.github.proto4j.objection.serial.SequenceSerializer;

public class MultiArrayTest {

    public static void main(String[] args) {
        int[][][] ints = new int[4][5][3];

        ObjectSerializer serializer = new SequenceSerializer.MultiDimensionArraySerializer(ints.getClass());
    }

}
