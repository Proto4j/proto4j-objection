package org.proto4j.test.objection.model; //@date 28.08.2022

import org.proto4j.objection.annotation.Serialize;

import java.util.Map;

@Serialize
public class Maps {

    private Map<String, Long> map;

    public Maps() {
    }

    public Maps(Map<String, Long> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Maps maps = (Maps) o;

        return map != null ? map.equals(maps.map) : maps.map == null;
    }

}
