package uk.co.thinkofdeath.command;

import java.util.List;

public class Util {

    public static <T> boolean same(List<T> a, List<T> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (Object el : a) {
            if (!b.contains(el)) {
                return false;
            }
        }
        return true;
    }
}
