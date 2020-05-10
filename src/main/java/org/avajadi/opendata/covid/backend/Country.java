package org.avajadi.opendata.covid.backend;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Country {
    private static final Map<String, Country> countries = new HashMap<>();
    private final String name;

    public Country(String name) {
        this.name = name;
    }

    public static Country get(String name) {
        return countries.get(name);
    }

    public static Collection<Country> countries() {
        return countries.values();
    }

    public static Country of(String name) {
        Country c = countries.get(name);
        if (null == c) {
            c = new Country(name);
            countries.put(name, c);
        }
        return c;
    }

    public static void main(String[] a) {
        LinkedList<Integer> ll = new LinkedList<>();
        System.out.println(ll.size());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
