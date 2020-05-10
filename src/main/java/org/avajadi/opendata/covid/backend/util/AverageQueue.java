package org.avajadi.opendata.covid.backend.util;

import java.util.LinkedList;
import java.util.OptionalDouble;

public class AverageQueue {
    private final LinkedList<Double> queue;
    private final int maxLength;

    public AverageQueue(int maxLength) {
        this.maxLength = maxLength;
        queue = new LinkedList<>();
    }

    public void add(Double item) {
        if (maxLength == size()) {
            queue.removeLast();
        }
        queue.addFirst(item);
    }

    public int size() {
        return queue.size();
    }

    public double average() {
        OptionalDouble opt = queue.stream().mapToDouble(value -> value).average();
        if (opt.isPresent()) {
            return opt.getAsDouble();
        }
        return 0;
    }
}
