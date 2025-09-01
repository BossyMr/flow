package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

/**
 * A {@code StringType} represents a string type.
 */
public final class StringType implements ValueType {

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
        return sort = manager.getStringSort();
    }

    @Override
    public int hashCode() {
        return StringType.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringType;
    }

    @Override
    public String toString() {
        return "string";
    }
}
