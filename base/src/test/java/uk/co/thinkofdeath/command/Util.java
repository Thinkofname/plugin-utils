package uk.co.thinkofdeath.command;

import java.util.List;

public class Util {

    public static <T> void same(List<T> a, List<T> b) {
        if (a.size() != b.size()) {
            throw new AssertionError("a != b");
        }
        for (T el : a) {
            if (!b.contains(el)) {
                throw new AssertionError("Couldn't find " + el);
            }
        }
    }
}
