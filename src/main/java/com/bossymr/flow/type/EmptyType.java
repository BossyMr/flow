package com.bossymr.flow.type;

import io.github.cvc5.Sort;
import io.github.cvc5.TermManager;

/**
 * An {@code EmptyType} represents a {@code void} type.
 */
public final class EmptyType implements ValueType {

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
        throw new IllegalStateException("a value cannot be of empty type");
    }

    @Override
    public int hashCode() {
        return EmptyType.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EmptyType;
    }

    @Override
    public String toString() {
        return "empty";
    }
}
