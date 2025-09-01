package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

/**
 * An {@code IntegerType} represents an integer value.
 */
public final class IntegerType implements NumericType {

    private Sort sort;

    @Override
    public boolean isStructure() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public Sort getSort(TermManager manager) {
        if (sort != null) {
            return sort;
        }
        return sort = manager.getIntegerSort();
    }

    @Override
    public int hashCode() {
        return IntegerType.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntegerType;
    }

    @Override public String toString() {
        return "integer";
    }
}
