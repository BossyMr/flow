package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

/**
 * A {@code BooleanType} represents a boolean type.
 */
public final class BooleanType implements ValueType {

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
    public int hashCode() {
        return BooleanType.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BooleanType;
    }

    @Override
    public Sort getSort(TermManager manager) {
        if (sort != null) {
            return sort;
        }
        return sort = manager.getBooleanSort();
    }

    @Override
    public String toString() {
        return "boolean";
    }
}
