package io.github.proto4j.test.objection.model; //@date 28.08.2022

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Lists implements Serializable {

    // this list will be de-serialized as an ArrayList
    private List<String> strings;

    private List<Integer> integers;

    public Lists() {
    }

    public Lists(List<String> strings, LinkedList<Integer> integers) {
        this.strings = strings;
        this.integers = integers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lists lists = (Lists) o;

        return strings.containsAll(lists.strings) && integers.containsAll(lists.integers);
    }

}
